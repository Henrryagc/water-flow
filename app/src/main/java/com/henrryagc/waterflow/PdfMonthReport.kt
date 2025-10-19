import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.text.TextPaint
import android.text.TextUtils
import android.util.Log
import com.henrryagc.waterflow.Client
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// In PdfMonthReport.kt
class PdfMonthReport(private val context: Context) {

    private val A4_WIDTH = 595
    private val A4_HEIGHT = 842
    private val MARGIN = 40f
    private val LINE_SPACING = 15f
    private val TEXT_SIZE_TITLE = 18f
    private val TEXT_SIZE_HEADER = 12f
    private val TEXT_SIZE_NORMAL = 10f
    private val TEXT_SIZE_FOOTER_HEADER = 8f // For page number and date

    private var latestFileNameCreated = ""
    data class TableCell(val text: String, val widthFraction: Float, val paint: Paint)

    fun createMonthlyReport(clients: List<Client>, month: Int, year: Int) {
        val document = PdfDocument()
        var currentPageNumber = 0 // Initialize page number

        // --- Paints ---
        val titlePaint = TextPaint().apply {
            color = Color.BLACK
            textSize = TEXT_SIZE_TITLE
            isAntiAlias = true
            isFakeBoldText = true
        }
        val headerPaint = TextPaint().apply {
            color = Color.BLACK
            textSize = TEXT_SIZE_HEADER
            isAntiAlias = true
            isFakeBoldText = true
        }
        val normalPaint = TextPaint().apply {
            color = Color.DKGRAY
            textSize = TEXT_SIZE_NORMAL
            isAntiAlias = true
        }
        val dateTimePaint = TextPaint().apply { // For top-right date/time
            color = Color.GRAY
            textSize = TEXT_SIZE_FOOTER_HEADER
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT // Align text to the right
        }
        val pageNumberPaint = TextPaint().apply { // For bottom-right page number
            color = Color.GRAY
            textSize = TEXT_SIZE_FOOTER_HEADER
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT // Align text to the right
        }

        // --- Date/Time for Header ---
        val currentDateTime = SimpleDateFormat(
            "dd/MM/yyyy HH:mm:ss",
            Locale.getDefault()
        ).format(Calendar.getInstance().time)

        // --- Start Page Logic ---
        var page: PdfDocument.Page? = null
        var canvas: Canvas? = null
        var currentY = MARGIN

        fun startNewPage() {
            if (page != null) {
                document.finishPage(page)
            }
            currentPageNumber++
            val pageInfo =
                PdfDocument.PageInfo.Builder(A4_WIDTH, A4_HEIGHT, currentPageNumber).create()
            page = document.startPage(pageInfo)
            canvas = page!!.canvas
            currentY = MARGIN

            // Draw current date/time at top right
            canvas?.drawText(currentDateTime, A4_WIDTH - MARGIN, MARGIN, dateTimePaint)
            // Adjust currentY if date/time takes significant space, for now, we assume title is lower
        }

        startNewPage() // Start the first page

        // --- Report Title ---
        val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(
            Calendar.getInstance().apply { set(Calendar.MONTH, month - 1) }.time
        )
        val reportTitle = "Gotaa Reporte Mensual - $monthName $year"
        // Ensure title is below the date/time if MARGIN is small
        currentY =
            MARGIN + dateTimePaint.fontSpacing + (LINE_SPACING / 2) // Adjust currentY after datetime
        canvas?.drawText(reportTitle, MARGIN, currentY, titlePaint)
        currentY += titlePaint.fontSpacing * 2

        // --- Table Headers ---
        val headers = listOf(
            TableCell("Fecha", 0.20f, headerPaint),
            TableCell("Cliente", 0.25f, headerPaint),
            TableCell("Mz/Lt", 0.15f, headerPaint),
            TableCell("Cilindros.", 0.15f, headerPaint),
            TableCell("Baldes.", 0.15f, headerPaint),
            TableCell("Total", 0.10f, headerPaint)
        )

        currentY = drawTableRow(canvas!!, headers, MARGIN, currentY, A4_WIDTH - 2 * MARGIN)
        currentY += LINE_SPACING / 2

        // --- Table Data ---
        var totalCylinders = 0.0
        var totalBuckets = 0.0
        var grandTotal = 0.0

        for ((index, client) in clients.withIndex()) {
            val estimatedRowHeight = normalPaint.fontSpacing * 2 // Estimate for 2 lines per row
            if (currentY + estimatedRowHeight > A4_HEIGHT - MARGIN - (pageNumberPaint.fontSpacing + LINE_SPACING)) { // Check space before footer
                drawPageNumber(
                    canvas!!,
                    currentPageNumber.toString(),
                    pageNumberPaint
                ) // Draw page number on current page before finishing
                startNewPage()
                // Redraw headers on new page
                currentY = drawTableRow(canvas!!, headers, MARGIN, currentY, A4_WIDTH - 2 * MARGIN)
                currentY += LINE_SPACING / 2
            }

            val displayDate = try {
                val parser = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.US)
                val formatter = SimpleDateFormat("dd/MM/yy", Locale.US)
                formatter.format(parser.parse(client.date)!!)
            } catch (e: Exception) {
                client.date.split(" ")[0]
            }

            val rowData = listOf(
                TableCell(displayDate, headers[0].widthFraction, normalPaint),
                TableCell(client.fullName ?: "", headers[1].widthFraction, normalPaint),
                TableCell(
                    "${client.mz ?: ""}/${client.lt ?: ""}",
                    headers[2].widthFraction,
                    normalPaint
                ),
                TableCell(client.cylinder ?: "0", headers[3].widthFraction, normalPaint),
                TableCell(client.bucket ?: "0", headers[4].widthFraction, normalPaint),
                TableCell(client.total ?: "0", headers[5].widthFraction, normalPaint)
            )
            currentY = drawTableRow(canvas!!, rowData, MARGIN, currentY, A4_WIDTH - 2 * MARGIN)

            totalCylinders += client.cylinder?.toDoubleOrNull() ?: 0.0
            totalBuckets += client.bucket?.toDoubleOrNull() ?: 0.0
            grandTotal += client.total?.toDoubleOrNull() ?: 0.0
        }

        // --- Summary Section ---
        val summaryEstimatedHeight = headerPaint.fontSpacing * 4 // title + 3 lines
        if (currentY + summaryEstimatedHeight > A4_HEIGHT - MARGIN - (pageNumberPaint.fontSpacing + LINE_SPACING)) { // Check space before footer
            drawPageNumber(canvas!!, currentPageNumber.toString(), pageNumberPaint)
            startNewPage()
        }

        currentY += LINE_SPACING // Add a bit more space before summary
        canvas?.drawText(
            "Resumen:",
            MARGIN,
            currentY,
            titlePaint.apply { textSize = TEXT_SIZE_HEADER + 2f })
        currentY += headerPaint.fontSpacing
        canvas?.drawText(
            "Total Cilindros: ${String.format("%.2f", totalCylinders)}",
            MARGIN + 10f,
            currentY,
            normalPaint
        )
        currentY += normalPaint.fontSpacing
        canvas?.drawText(
            "Total Baldes: ${String.format("%.2f", totalBuckets)}",
            MARGIN + 10f,
            currentY,
            normalPaint
        )
        currentY += normalPaint.fontSpacing
        canvas?.drawText(
            "Total: ${String.format("%.2f", grandTotal)}",
            MARGIN + 10f,
            currentY,
            normalPaint
        )

        // --- Finish and Save PDF ---
        if (page != null) {
            drawPageNumber(
                canvas!!,
                currentPageNumber.toString(),
                pageNumberPaint
            ) // Draw page number on the last page
            document.finishPage(page)
        }

        val directory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val subDirectory = File(directory, "ReportesGotaa")
        if (!subDirectory.exists()) {
            subDirectory.mkdirs()
        }

        val timestamp = SimpleDateFormat(
            "yyyy_MM_dd_HHmmss",
            Locale.getDefault()
        ).format(Calendar.getInstance().time)
        val fileName = "Gotaa_${year}_${String.format("%02d", month)}_reporte-$timestamp.pdf"
        val filePath = File(subDirectory, fileName)
        latestFileNameCreated = "El documento se guardo en: \"${Environment.DIRECTORY_DOCUMENTS}/ReportesGotaa\" con nombre: \"$fileName\""
        try {
            document.writeTo(FileOutputStream(filePath))
            Log.i("PdfMonthReport", "PDF report saved to: ${filePath.absolutePath}")
        } catch (e: IOException) {
            Log.e("PdfMonthReport", "Error writing PDF: $e")
        }
        document.close()
    }

    private fun drawPageNumber(canvas: Canvas, pageNumText: String, pageNumberPaint: Paint) {
        canvas.drawText(
            "Página $pageNumText",
            A4_WIDTH - MARGIN,
            A4_HEIGHT - MARGIN, // Position at bottom right
            pageNumberPaint
        )
    }

    private fun drawTableRow(
        canvas: Canvas,
        cells: List<TableCell>,
        startX: Float,
        startY: Float,
        rowTotalWidth: Float
    ): Float {
        var currentX = startX
        var maxHeightInRow = 0f

        for (cell in cells) {
            val cellWidth = rowTotalWidth * cell.widthFraction
            val textBounds = Rect()
            // For TextPaint, getTextBounds might not be perfectly accurate for wrapped text.
            // StaticLayout is better for height calculation of wrapped text.
            // This is a simplified estimation.
            var lines = 1
            if (cell.paint.measureText(cell.text) > cellWidth && cellWidth > 0) {
                // Crude line estimation, StaticLayout would be more accurate
                lines = (cell.paint.measureText(cell.text) / cellWidth).toInt() + 1
            }
            val cellHeight =
                lines * cell.paint.fontSpacing + (LINE_SPACING / 2) // Add padding within cell
            if (cellHeight > maxHeightInRow) {
                maxHeightInRow = cellHeight
            }
        }
        // maxHeightInRow += LINE_SPACING / 2 // Already added padding within cell calculation

        for (cell in cells) {
            val cellWidth = rowTotalWidth * cell.widthFraction
            val textY =
                startY + (maxHeightInRow / 2) - (cell.paint.descent() + cell.paint.ascent()) / 2 // Centering

            val ellipsizedText = TextUtils.ellipsize(
                cell.text,
                cell.paint as TextPaint,
                cellWidth - (MARGIN / 4),
                TextUtils.TruncateAt.END
            )
            canvas.drawText(
                ellipsizedText.toString(),
                currentX + (MARGIN / 8),
                textY,
                cell.paint
            )
            currentX += cellWidth
        }
        return startY + maxHeightInRow // Return Y for next row (removed extra LINE_SPACING/2 here as it was added in cellHeight)
    }

    fun getLatestFileNameCreated() = latestFileNameCreated

}