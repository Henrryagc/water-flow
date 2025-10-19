package com.henrryagc.waterflow

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class PdfCreator {




    //@RequiresApi(Build.VERSION_CODES.R)
    fun createPdf(someText: String) {
        // create a new document


        /*
        https://www.pdftron.com/documentation/samples/kt/ElementBuilderTest
        https://developer.android.com/training/data-storage/shared/documents-files
         */


        val document = PdfDocument()
        // crate a page description
        var pageInfo = PageInfo.Builder(300, 600, 1).create()
        // start a page
        var page = document.startPage(pageInfo)
        var canvas: Canvas = page.canvas
        var paint = Paint()
        paint.color = Color.RED
        canvas.drawCircle(50f, 50f, 30f, paint)
        paint.color = Color.BLACK
        canvas.drawText(someText, 80f, 50f, paint)
        //canvas.draw
        // finish the page
        document.finishPage(page)
        // draw text on the graphics object of the page
        // Create Page 2
        pageInfo = PageInfo.Builder(300, 600, 2).create()
        page = document.startPage(pageInfo)
        canvas = page.canvas
        paint = Paint()
        paint.color = Color.BLUE
        canvas.drawCircle(100f, 100f, 100f, paint)
        document.finishPage(page)
        // write the document content
        val directoryPath = Environment.getDataDirectory().toString() + "/mipdf/"//Environment.getRootDirectory().path + "/mypdf/"
        val folder = File(directoryPath);
        Log.i("Info:",folder.toString())
        if (!folder.exists()){
            folder.mkdirs()
            Log.d("hola","holalal")
        }

        val path = System.getProperty("user.dir")
        println("Working Directory = $path")
        //Log.e("Error",Environment.getRootDirectory().path + "/mypdf/");
        //Log.i("Info", Environment.getStorageDirectory().path +"/s")
        Log.i("Info", Environment.DIRECTORY_DOCUMENTS+"/mipdf/")
        //Log.i("Info", Environment.getExternalStorageState())
        Log.i("Info:", Environment.getDataDirectory().toString() )

        /*val file = File(directoryPath)
        if (!file.exists()) {
            file.mkdirs()
        }*/

        //val targetPdf = "$directoryPath/test-2.pdf"
        val filePath = File(directoryPath,"test2.pdf")
        try {
            document.writeTo(FileOutputStream(filePath))
            //Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Log.e("main", "error $e")
            //Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show()
        }
        // close the document
        document.close()
    }
}