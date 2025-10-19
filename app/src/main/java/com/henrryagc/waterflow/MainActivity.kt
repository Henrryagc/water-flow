package com.henrryagc.waterflow

import android.content.Intent
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import com.henrryagc.waterflow.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val CREATE_FILE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNewRecord.setOnClickListener {
            val intent = Intent(this, InsertActivity::class.java)
            startActivity(intent)
        }

        binding.btnSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        binding.btnReports.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            startActivity(intent)
        }
    }



//    fun onGoInsertActivity(view: View) {
//        startActivity(Intent(this, InsertActivity::class.java))
//    }
//
//    fun onGoSearchActivity(view: View) {
//        startActivity(Intent(this, SearchActivity::class.java))
//    }
//
//    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
//        ActivityCompat.finishAffinity(this)
//        finish()
//        return super.getOnBackInvokedDispatcher()
//    }

    fun onTestCreatePdf(view: View) {
        // create a new document
       /* val document: PdfDocument =  PdfDocument()

        // create a page description
        val pageInfo: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(100, 100, 1).create();

        // start a page
        var page: PdfDocument.Page = document.startPage(pageInfo);

        // draw something on the page
        val content = view // View  = getContentView()
        content.draw(page.getCanvas());

        // finish the page
        document.finishPage(page);
        //. . .
        // add more pages
        //. . .
        // write the document content
        document.writeTo(getOutputStream());

        // close the document
        document.close();*/

        /*val document =  PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(100, 100, 1).create()
        val page = document.startPage(pageInfo)

        view.draw(page.canvas)

        document.finishPage(page)

        document.writeTo(openFileOutput("s", MODE_APPEND))

        document.close()*/

        //val pdfCreator = PdfCreator();
        //pdfCreator.createPdf("texto de algo")

        //createFile(Environment.getDataDirectory().toString())

        Toast.makeText(this,
            "No implementado por le momento, sorry :C",
            Toast.LENGTH_SHORT)
            .show()

    }

    // Request code for creating a PDF document.
    //val CREATE_FILE = 1

    private fun createFile(pickerInitialUri: String) {

        val date = LocalDate.now()
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "reporte$date.pdf")

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        startActivityForResult(intent, CREATE_FILE)
    }
}