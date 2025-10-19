package com.henrryagc.waterflow

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.net.ParseException
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Locale

internal class RecyclerAdapter(private var mContext: Context,
                               private var itemsList: List<Client> ):
        RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {

    //val intent

    internal inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val btnEdit: FloatingActionButton = view.findViewById(R.id.btn_card_edit)
        //val btnDelete: Button = view.findViewById(R.id.btn_card_delete)
        val cardView: CardView = view.findViewById(R.id.card_item_search)

        var fullName: TextView = view.findViewById(R.id.tv_card_full_name)
        var mz: TextView = view.findViewById(R.id.tv_card_mz)
        var lt: TextView = view.findViewById(R.id.tv_card_lt)
        var date: TextView = view.findViewById(R.id.tv_card_date)
        var cylinder: TextView = view.findViewById(R.id.tv_card_cylinder)
        var bucket: TextView = view.findViewById(R.id.tv_card_bucket)
        var total: TextView = view.findViewById(R.id.tv_card_total)


   /*     init {
            btnEdit.setOnClickListener {

                val intent = Intent(mContext, InsertActivity::class.java)
                //intent.putExtra("client", item)
                mContext.startActivity()
            }
        }*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.items_search, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemsList[position]

        // Define the input and output date formats
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault())
        var formattedDate = item.date // Default to original if parsing fails
        try {
            val dateObj = inputFormat.parse(item.date)
            if (dateObj != null) {
                formattedDate = outputFormat.format(dateObj)
            }
        } catch (e: ParseException) {
            e.printStackTrace() // Log the error or handle it as needed
            // Keep formattedDate as the original item.date if parsing fails
        }

        holder.fullName.text = item.fullName
        holder.mz.text = "MZ: ${item.mz}"
        holder.lt.text = "LT: " + item.lt
        holder.date.text = formattedDate
        holder.cylinder.text = "Cilindros: " + item.cylinder
        holder.bucket.text = "Valdes: " + item.bucket
        holder.total.text = "TOTAL: S/. " + item.total

        holder.btnEdit.setOnClickListener{
            val clientData = Client(item.idClient, item.fullName, item.mz, item.lt, item.date, item.cylinder, item.bucket, item.total)
            val intent = Intent(mContext, InsertActivity::class.java)
            intent.putExtra("client", clientData)
            mContext.startActivity(intent)
        }
/*
        holder.btnDelete.setOnClickListener {
            println("btn delete ${item.idClient}")
            holder.cardView.isVisible = false

            val insertApp = SearchActivity()
            insertApp.deleteClient(item.idClient, mContext)
        }*/

    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}
/*
private operator fun <T> LiveData<T>.get(position: Int): CharSequence {


    return

}*/
