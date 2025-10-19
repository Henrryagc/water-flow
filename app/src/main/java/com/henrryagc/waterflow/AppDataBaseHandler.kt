package com.henrryagc.waterflow

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.icu.util.Calendar
import androidx.fragment.app.add
import java.text.SimpleDateFormat
import java.util.Locale

class AppDataBaseHandler(context: Context): SQLiteOpenHelper(context,
    DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "waterpg"
        private const val DB_TABLE = "client"

        private const val idClient = "idClient"
        private const val fullName = "fullName"
        private const val mz = "mz"
        private const val lt = "lt"
        private const val date = "date"
        private const val cylinder = "cylinder"
        private const val bucket = "bucket"
        private const val total = "total"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            create table $DB_TABLE (
            $idClient integer primary key autoincrement,
            $fullName varchar(110),
            $mz varchar(5),
            $lt varchar(5),
            $date varchar(12),
            $cylinder varchar(3),
            $bucket varchar(3),
            $total varchar(6)            
            )
        """.trimIndent()

        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun insertClient(client: Client): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        // Format date to iso
        //val dateIso = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(client.date)

        //contentValues.put(idClient, client.idClient)
        contentValues.put(fullName, client.fullName)
        contentValues.put(mz, client.mz)
        contentValues.put(lt, client.lt)
        contentValues.put(date, client.date)
        contentValues.put(cylinder, client.cylinder)
        contentValues.put(bucket, client.bucket)
        contentValues.put(total, client.total)

        val success = db.insert(DB_TABLE,null,contentValues)
        db.close()

        return success
    }

    //@SuppressLint("Recycle")
    fun getAllClients(): List<Client>{
        val clientList: ArrayList<Client> = ArrayList()

        val sql = "select * from $DB_TABLE order by $idClient DESC"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(sql, null)
        } catch (e: SQLiteException) {
            db.execSQL(sql)
            db.close()
            cursor?.close()
            return ArrayList()
        }

        if (cursor.moveToFirst()){
            do {
                val xid: Int = cursor.getInt(with(cursor) { getColumnIndex(idClient) })
                val xname: String = cursor.getString(with(cursor) { getColumnIndex(fullName) })
                val xmz: String = cursor.getString(with(cursor) { getColumnIndex(mz) })
                val xlt: String = cursor.getString(with(cursor) { getColumnIndex(lt) })
                val xdate: String = cursor.getString(with(cursor) { getColumnIndex(date) })
                val xcylinder: String = cursor.getString(with(cursor) { getColumnIndex(cylinder) })
                val xbucket: String = cursor.getString(with(cursor) { getColumnIndex(bucket) })
                val xtotal: String = cursor.getString(with(cursor) { getColumnIndex(total) })

                val newClient = Client(
                    idClient = xid, fullName = xname, mz = xmz, lt = xlt,
                    date = xdate, cylinder = xcylinder, bucket = xbucket, total = xtotal
                )
                clientList.add(newClient)

            } while (cursor.moveToNext())
        }
        //cursor.close()
        //db.close()
        return clientList
    }

    fun getAllClientsByValues(value: String, data: String): List<Client>{
        val clientList: ArrayList<Client> = ArrayList()

        val sql = "select * from $DB_TABLE where $value like '$data%'"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(sql, null)

        } catch (e: SQLiteException) {
            db.execSQL(sql)
            db.close()
            cursor?.close()
            return ArrayList()
        }

        if (cursor.moveToFirst()){
            do {
                val xid: Int = cursor.getInt(with(cursor) { getColumnIndex(idClient) })
                val xname: String = cursor.getString(with(cursor) { getColumnIndex(fullName) })
                val xmz: String = cursor.getString(with(cursor) { getColumnIndex(mz) })
                val xlt: String = cursor.getString(with(cursor) { getColumnIndex(lt) })
                val xdate: String = cursor.getString(with(cursor) { getColumnIndex(date) })
                val xcylinder: String = cursor.getString(with(cursor) { getColumnIndex(cylinder) })
                val xbucket: String = cursor.getString(with(cursor) { getColumnIndex(bucket) })
                val xtotal: String = cursor.getString(with(cursor) { getColumnIndex(total) })

                val newClient = Client(
                    idClient = xid, fullName = xname, mz = xmz, lt = xlt,
                    date = xdate, cylinder = xcylinder, bucket = xbucket, total = xtotal
                )
                clientList.add(newClient)

            } while (cursor.moveToNext())
        }
        //cursor.close()
        //db.close()
        return clientList
    }

    fun updateClient(client: Client): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(fullName, client.fullName)
        contentValues.put(mz, client.mz)
        contentValues.put(lt, client.lt)
        contentValues.put(date, client.date)
        contentValues.put(cylinder, client.cylinder)
        contentValues.put(bucket, client.bucket)
        contentValues.put(total, client.total)

        val success = db.update(DB_TABLE, contentValues, "idClient="+client.idClient, null)
        db.close()

        return success
    }

    fun deleteClient(id: Int): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(idClient, id)

        val success = db.delete(DB_TABLE, "idClient=$id", null)
        db.close()

        return success
    }

    // In your AppDataBaseHandler.kt (Conceptual - adapt to your actual schema)
// ... (other parts of your AppDataBaseHandler)
// Assuming your 'Client' data class has a 'date' field (String)
// and your table has a column for this date.
// IMPORTANT: Storing dates as strings like "dd/MM/yyyy hh:mm:ss a" is NOT ideal for querying.
// It's much better to store dates as:
//  1. INTEGER (Unix timestamp - seconds or milliseconds since epoch)
//  2. TEXT in ISO 8601 format ("YYYY-MM-DD HH:MM:SS") for easier sorting and querying.
// If you stick with your current string format, querying for a month is more complex.
// OPTION 1: If date is stored as "dd/MM/yyyy hh:mm:ss a" (less efficient)
    fun getClientsForMonth(month: Int, year: Int): ArrayList<Client> {
        val clientList = ArrayList<Client>()
        val db = this.readableDatabase
        // This query is inefficient for string dates. month and year are 1-based.
        // We're looking for '%/0M/YYYY%' where M is the month.
        val monthStr = if (month < 10) "0$month" else month.toString()
        val query =
            "SELECT * FROM $DB_TABLE WHERE $date LIKE '%/$monthStr/$year%'"
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: SQLiteException) {
            db.execSQL(query) // This seems incorrect for a SELECT, should handle exception
            return ArrayList()
        }



        if (cursor.moveToFirst()) {
            do {
                val xid: Int = cursor.getInt(with(cursor) { getColumnIndex(idClient) })
                val xname: String = cursor.getString(with(cursor) { getColumnIndex(Companion.fullName) })
                val xmz: String = cursor.getString(with(cursor) { getColumnIndex(mz) })
                val xlt: String = cursor.getString(with(cursor) { getColumnIndex(lt) })
                val xdate: String = cursor.getString(with(cursor) { getColumnIndex(date) })
                val xcylinder: String = cursor.getString(with(cursor) { getColumnIndex(cylinder) })
                val xbucket: String = cursor.getString(with(cursor) { getColumnIndex(bucket) })
                val xtotal: String = cursor.getString(with(cursor) { getColumnIndex(total) })

                val newClient = Client(
                    idClient = xid, fullName = xname, mz = xmz, lt = xlt,
                    date = xdate, cylinder = xcylinder, bucket = xbucket, total = xtotal
                )
                clientList.add(newClient)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return clientList
    }

    // OPTION 2: If date is stored as TEXT in "YYYY-MM-DD HH:MM:SS" format (BETTER)

    fun getClientsForMonthISO(month: Int, year: Int): ArrayList<Client> {
        val clientList = ArrayList<Client>()
        val db = this.readableDatabase
        // month is 1-based (January=1), so format it to two digits
        val monthFormatted = String.format("%02d", month)
        val startDate = "$year-$monthFormatted-01"
        // To get the end date, find the first day of the next month
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1) // month is 0-based in Calendar
        calendar.add(Calendar.MONTH, 1)
        val nextMonthYear = calendar.get(Calendar.YEAR)
        val nextMonth = String.format("%02d", calendar.get(Calendar.MONTH) + 1) // +1 because Calendar month is 0-based
        val endDate = "$nextMonthYear-$nextMonth-01"


        // Query for dates between the start of the target month and the start of the next month
        val query =
            "SELECT * FROM $DB_TABLE WHERE $date >= ? AND $date < ?"

        println("Query report: $query")

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(query, arrayOf(startDate, endDate))
        } catch (e: SQLiteException) {
            // Handle exception appropriately
            return ArrayList()
        }

        if (cursor.moveToFirst()) {
            do {
                val xid: Int = cursor.getInt(with(cursor) { getColumnIndex(idClient) })
                val xname: String = cursor.getString(with(cursor) { getColumnIndex(fullName) })
                val xmz: String = cursor.getString(with(cursor) { getColumnIndex(mz) })
                val xlt: String = cursor.getString(with(cursor) { getColumnIndex(lt) })
                val xdate: String = cursor.getString(with(cursor) { getColumnIndex(date) })
                val xcylinder: String = cursor.getString(with(cursor) { getColumnIndex(cylinder) })
                val xbucket: String = cursor.getString(with(cursor) { getColumnIndex(bucket) })
                val xtotal: String = cursor.getString(with(cursor) { getColumnIndex(total) })

                val client = Client(
                    idClient = xid, fullName = xname, mz = xmz, lt = xlt,
                    date = xdate, cylinder = xcylinder, bucket = xbucket, total = xtotal
                )
                clientList.add(client)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return clientList
    }
}