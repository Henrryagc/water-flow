package com.henrryagc.waterflow

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
//import androidx.core.view.get
import androidx.lifecycle.LiveData
//import androidx.lifecycle.Observer
//import androidx.lifecycle.Transformations
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

//import com.henrryagc.waterappmorrosama.cliente.AppDatabase
//import com.henrryagc.waterappmorrosama.Cliente

class SearchActivity : AppCompatActivity() {
    private val itemList = ArrayList<String>()
    private lateinit var recyclerAdapter: RecyclerAdapter

    //private lateinit var database: AppDatabase
    private lateinit var client: Client
    private lateinit var clientLiveData: LiveData<Client>
    private lateinit var allClient: LiveData<List<Client>>
    private lateinit var totalClient: List<Client>
    private val list = arrayOf("todos", "fullName", "total", "date")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val mainLayout = findViewById<LinearLayout>(R.id.linear_search_layout)

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                left = insets.left,
                top = insets.top,
                bottom = insets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }

        title = "LISTA DE VENTAS"

        val database = AppDataBaseHandler(this)
        var clients: List<Client> = database.getAllClients()

        val spinner: Spinner = findViewById(R.id.spinner)
        val btnSearch = findViewById<Button>(R.id.btn_search)
        val etSearchData = findViewById<EditText>(R.id.et_search_data)

        ArrayAdapter.createFromResource(
            this,
            R.array.client_data_array,
            android.R.layout.simple_spinner_item
            //android.R.layout.simple_spinner_item

        ). also {
            adapter ->
            adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
                //android.R.layout.simple_spinner_dropdown_item
            )
            spinner.adapter = adapter
        }

        //println("Hello from select all clients")
        //for (client in clients) println(client.idClient)

        //database = AppDatabase.getDatabase(this)
        //val idClient = intent.getIntExtra("id", 0)
        //clientLiveData = database.client().getClient(idClient)
        /*
        clientLiveData.observe(this, Observer {
            client = it
        })*/
        /*
        allClient = database.client().getAllClient()
        allClient.observe(this, {
            totalClient = it
        })*/

        btnSearch.setOnClickListener {
            val select = spinner.selectedItemPosition
            val data = etSearchData.text.toString()
            println("$select $data")

            clients = if (select > 0)
                database.getAllClientsByValues(list[select], data)
            else
                database.getAllClients()

            val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
            recyclerAdapter = RecyclerAdapter(this, clients)

            val layoutManager = LinearLayoutManager(applicationContext)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = recyclerAdapter
            recyclerAdapter.notifyDataSetChanged()
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerAdapter = RecyclerAdapter(this, clients)

        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recyclerAdapter
        recyclerAdapter.notifyDataSetChanged()
        //prepareItems()

    }

    private fun prepareItems(){

        /*val clienttes: LiveData<Cliente> = Transformations.map(allClient){
            client
        }
        */
        //allClient = database.client().getAllClient()
        allClient.observe(this, {
            totalClient = it
        })
        itemList.add("Item 1")
        itemList.add("Item 2")
        itemList.add("Item 3")
        itemList.add("Item 4")
        itemList.add("Item 5")
        itemList.add("Item 6")
        itemList.add("Item 7")
        itemList.add("Item 8")
        itemList.add("Item 9")
        itemList.add("Item 10")
        itemList.add("Item 11")
        itemList.add("Item 12")
        itemList.add("Item 13")
        recyclerAdapter.notifyDataSetChanged()
    }

    fun deleteClient(id: Int, context: Context) {
        if (id > 0)
        {

        } else {
            println("Error delete")
        }
    }

    fun finishSearchActivity(){
        finish()
    }
}