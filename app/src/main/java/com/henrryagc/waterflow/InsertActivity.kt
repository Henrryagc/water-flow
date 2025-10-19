package com.henrryagc.waterflow

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList

//import com.henrryagc.waterappmorrosama.cliente.AppDatabase
//import com.henrryagc.waterappmorrosama.cliente.Cliente
//import kotlinx.android.synthetic.main.activity_insert.*
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch


class InsertActivity : AppCompatActivity() {

    //private lateinit var binding: ResultProfile

    private var id: String = "0"
    //val buttonDelete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert)
        //binding = ResultProfileBinding.inflate(layoutInflater)
        //val database = AppDatabase.getDatabase(this)
        val mainLayout = findViewById<LinearLayout>(R.id.linear_insert_layout)

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                left = insets.left,
                top = insets.top,
                bottom = insets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }



        title = "NUEVO REGISTRO"

        val database = AppDataBaseHandler(this)

        val btnSave = findViewById<Button>(R.id.btn_save)
        val btnDelete = findViewById<Button>(R.id.button4)
        btnDelete.isVisible = false

        val etFullName = findViewById<EditText>(R.id.et_fullName)
        val etMz = findViewById<EditText>(R.id.et_mz)
        val etLt = findViewById<EditText>(R.id.et_lt)
        val etDate = findViewById<EditText>(R.id.et_date)
        val etCylinder = findViewById<EditText>(R.id.et_cylinder)
        val etBucket = findViewById<EditText>(R.id.et_bucket)
        val etTotal = findViewById<EditText>(R.id.et_total)
        val currentDate = Date()
        @SuppressLint("SimpleDateFormat")
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a")
        val currentDateFormat = sdf.format(currentDate)
        etDate.setText(currentDateFormat)



        btnSave.setOnClickListener{
            if (id == "0") {
                // NEW CLIENT
                val fullName = etFullName.text.toString()
                val mz = etMz.text.toString()
                val lt = etLt.text.toString() //.toInt()
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDate)
                val cylinder = etCylinder.text.toString() //.toInt()
                val bucket = etBucket.text.toString()//.toInt()
                val total = etTotal.text.toString()//.toDouble()

                val client = Client(0, fullName, mz, lt, date, cylinder, bucket, total)

                if (fullName.isEmpty() || mz.isEmpty() || lt.isEmpty() ||
                    cylinder.isEmpty() || bucket.isEmpty() || total.isEmpty()
                ) {
                    Toast.makeText(applicationContext, "Llenar todos los campos", Toast.LENGTH_LONG)
                        .show()
                } else {
                    val status = database.insertClient(client)
                    if (status > -1) {
                        Toast.makeText(applicationContext, "El registro se ha guardado correctamente", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, SearchActivity::class.java))
                        finish()
                    } else
                        Toast.makeText(applicationContext, "Ocurrio un error al intentar guardar el registro", Toast.LENGTH_SHORT).show()
                }
            } else {
                // UPDATE CLIENT
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Actualizar Registro")
                builder.setMessage("¿Desea guardar los cambios?")
                builder.apply {
                    setPositiveButton("SI"
                    ) { dialog, idDialog ->
                        val fullName = etFullName.text.toString()
                        val mz = etMz.text.toString()
                        val lt = etLt.text.toString() //.toInt()
                        val date = etDate.text.toString()
                        val cylinder = etCylinder.text.toString() //.toInt()
                        val bucket = etBucket.text.toString()//.toInt()
                        val total = etTotal.text.toString()//.toDouble()

                        val client =
                            Client(id.toInt(), fullName, mz, lt, date, cylinder, bucket, total)

                        val status = database.updateClient(client)

                        if (status > -1)
                            Toast.makeText(applicationContext,"Registro actualizado",Toast.LENGTH_LONG).show()
                       // println("Update client")
                    }
                    setNegativeButton("CANCELAR"
                    ) { dialog, id ->
                        // None
                    }
                }
                // Create the AlertDialog
                val alertDialog: AlertDialog = builder.create()
                alertDialog.show()
            }

        }
        val clientData: Client? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("client", Client::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("client") as? Client
        }

        println("List clients--- $clientData")

        if (clientData != null) {
            id = clientData.idClient.toString()
            etFullName.setText(clientData.fullName)
            //println("List clients edit---")
            etMz.setText(clientData.mz)
            etLt.setText(clientData.lt)
            etDate.setText(clientData.date)
            etCylinder.setText(clientData.cylinder)
            etBucket.setText(clientData.bucket)
            etTotal.setText(clientData.total)

            btnDelete.isVisible = true
            //btnSave.backgroundTintList = ContextCompat.getColor(this, R.color.black)//"#FB8C00"
            btnSave.setBackgroundColor(ContextCompat.getColor(this, R.color.naranja))
            btnSave.text = "Actualizar"
            title = "ACTUALIZAR REGISTRO"
            btnDelete.setOnClickListener {

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Eliminar")
                builder.setMessage("¿Desea eliminar el registro?")
                builder.apply {
                    setPositiveButton("SI"
                    ) { dialog, idDialog ->

                        val status = database.deleteClient(id.toInt())
                        id = "0" // Set zero to id for add new client
                        title = "NUEVO REGISTRO"
                        if (status > -1)
                            Toast.makeText(applicationContext,"Se ha eliminado correctamente", Toast.LENGTH_LONG).show()
                        else
                            Toast.makeText(applicationContext,"Ocurrio un error al intentar eliminar", Toast.LENGTH_LONG).show()
                        //startActivity(Intent(this, InsertActivity::class.java))
                    }
                    setNegativeButton("CANCELAR"
                    ) { dialog, id ->
                        // None
                    }
                }
                // Create the AlertDialog
                val alertDialog: AlertDialog = builder.create()
                alertDialog.show()
            }
        }

    }
/*
    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }*/
}