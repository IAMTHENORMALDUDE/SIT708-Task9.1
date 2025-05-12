package com.example.lostandfound.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.lostandfound.model.Item

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "lostandfound.db"
        private const val DATABASE_VERSION = 2

        // Table name
        private const val TABLE_ITEMS = "items"

        // Column names
        private const val COLUMN_ID = "id"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_LOCATION = "location"
        private const val COLUMN_LATITUDE = "latitude"
        private const val COLUMN_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_ITEMS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TYPE TEXT NOT NULL,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_PHONE TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT NOT NULL,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_LOCATION TEXT NOT NULL,
                $COLUMN_LATITUDE REAL DEFAULT 0.0,
                $COLUMN_LONGITUDE REAL DEFAULT 0.0
            )
        """.trimIndent()
        
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Add new columns for latitude and longitude
            db.execSQL("ALTER TABLE $TABLE_ITEMS ADD COLUMN $COLUMN_LATITUDE REAL DEFAULT 0.0")
            db.execSQL("ALTER TABLE $TABLE_ITEMS ADD COLUMN $COLUMN_LONGITUDE REAL DEFAULT 0.0")
        } else {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_ITEMS")
            onCreate(db)
        }
    }

    // Insert a new item
    fun insertItem(item: Item): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TYPE, item.type)
            put(COLUMN_NAME, item.name)
            put(COLUMN_PHONE, item.phone)
            put(COLUMN_DESCRIPTION, item.description)
            put(COLUMN_DATE, item.date)
            put(COLUMN_LOCATION, item.location)
            put(COLUMN_LATITUDE, item.latitude)
            put(COLUMN_LONGITUDE, item.longitude)
        }
        
        val id = db.insert(TABLE_ITEMS, null, values)
        db.close()
        return id
    }

    // Get all items
    fun getAllItems(): List<Item> {
        val itemList = mutableListOf<Item>()
        val selectQuery = "SELECT * FROM $TABLE_ITEMS"
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        
        if (cursor.moveToFirst()) {
            do {
                val item = Item(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                    location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)),
                    latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                    longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))
                )
                itemList.add(item)
            } while (cursor.moveToNext())
        }
        
        cursor.close()
        db.close()
        return itemList
    }

    // Delete an item by ID
    fun deleteItem(itemId: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_ITEMS, "$COLUMN_ID = ?", arrayOf(itemId.toString()))
        db.close()
        return result
    }

    // Get item by ID
    fun getItemById(itemId: Int): Item? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_ITEMS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(itemId.toString()),
            null,
            null,
            null
        )

        var item: Item? = null
        if (cursor.moveToFirst()) {
            item = Item(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)),
                latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))
            )
        }
        
        cursor.close()
        db.close()
        return item
    }
}
