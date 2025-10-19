package com.henrryagc.waterflow

import PdfMonthReport
import android.Manifest
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ReportActivity : AppCompatActivity() { // Or integrate into SearchActivity

    private lateinit var databaseHandler: AppDataBaseHandler
    private lateinit var pdfCreator: PdfMonthReport

    private lateinit var progressBar: ProgressBar
    private lateinit var tvPdfCreatedDir: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report) // You'll need a layout for this

        databaseHandler = AppDataBaseHandler(this)
        pdfCreator = PdfMonthReport(this) // Pass context

        val btnGenerateReport = findViewById<Button>(R.id.btn_generate_monthly_report)
        // You'd typically have spinners or date pickers to select month and year
        val yearSpinner = findViewById<Spinner>(R.id.spinner_year)
        val monthSpinner = findViewById<Spinner>(R.id.spinner_month)
        progressBar = findViewById<ProgressBar>(R.id.pb_report_loading) // Referencia al ProgressBar
        tvPdfCreatedDir = findViewById<TextView>(R.id.tv_report_dir) // Referencia al TextView

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = ArrayList<String>()
        for (i in 0..5) { // Por ejemplo, los últimos 5 años + el actual
            years.add((currentYear - i).toString())
        }

        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = yearAdapter

        val months = resources.getStringArray(R.array.months_array) // Asume que tienes este array en strings.xml
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = monthAdapter
        monthSpinner.setSelection(Calendar.getInstance().get(Calendar.MONTH)) // Seleccionar mes actual por defecto


        btnGenerateReport.setOnClickListener {
            // Get selected month and year from spinners
            progressBar.visibility = ProgressBar.VISIBLE // Muestra el ProgressBar
            val selectedYear = yearSpinner.selectedItem.toString().toInt() // Example
            val selectedMonth =
                monthSpinner.selectedItemPosition + 1 // Assuming 0-indexed spinner for Jan-Dec

            // Permission Check (IMPORTANT for writing to External Storage)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_STORAGE_PERMISSION
                )
            } else {
                generateAndSaveReport(selectedMonth, selectedYear)
            }
        }
    }

    private fun generateAndSaveReport(month: Int, year: Int) {
        // Fetch data from database
        // IMPORTANT: Use the correct method based on how your dates are stored!
        // val clientsForReport = databaseHandler.getClientsForMonth(month, year) // If old date format
        val clientsForReport =
            databaseHandler.getClientsForMonthISO(month, year) // If ISO date format

        if (clientsForReport.isEmpty()) {
            Toast.makeText(this, "No se ha encontrado registros", Toast.LENGTH_LONG).show()
            progressBar.visibility = ProgressBar.INVISIBLE // Oculta el ProgressBar
            return
        }

        // Create PDF in a background thread to avoid blocking the UI
        lifecycleScope.launch(Dispatchers.IO) {
            tvPdfCreatedDir.text = ""
            pdfCreator.createMonthlyReport(clientsForReport, month, year)
            // Show toast or notification on the main thread after completion
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, "Reporte mensual generado", Toast.LENGTH_LONG)
                    .show()
                tvPdfCreatedDir.text = pdfCreator.getLatestFileNameCreated()
                // You could also offer to open the PDF here
                progressBar.visibility = ProgressBar.INVISIBLE // Oculta el ProgressBar
                progressBar.visibility = ProgressBar.GONE

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_STORAGE_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, now you can call the function that needs the permission
                // For example, if you stored month/year, call generateAndSaveReport again
                Toast.makeText(
                    this,
                    "Storage permission granted. Please try generating again.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Storage permission is required to save reports.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    companion object {
        private const val REQUEST_WRITE_STORAGE_PERMISSION = 101
    }
}