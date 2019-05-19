package com.celeritas.blacklist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import java.util.*

class DbWrapper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    val numbers: Table = Table(TableName.NUMBERS, this)
    val history: Table = Table(TableName.HISTORY, this)

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(numbers.sqlCreateEntries)
        db.execSQL(history.sqlCreateEntries)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "BlackList.db"
    }
}

data class Table(val name: TableName, val db: DbWrapper) {
    val sqlCreateEntries =
        "CREATE TABLE ${name.name} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "${Entry.NUMBER} TEXT, " +
                "${Entry.DATE} INTEGER, " +
                "${Entry.BLOCK_TYPE} INTEGER)"

    fun add(entry: DataItem) {
        val writable = db.writableDatabase

        writable.insert(name.name, null, entry.values())
    }

    fun remove(number: String) {
        val writable = db.writableDatabase

        writable.delete(name.name, "${Entry.NUMBER} LIKE ?", arrayOf(number))
    }

    fun update(entry: DataItem) {
        val writable = db.writableDatabase

        writable.update(name.name, entry.values(), "${Entry.NUMBER} LIKE ?", arrayOf(entry.number))
    }

    val items: List<DataItem>
        get() {
            val readable = db.readableDatabase

            val cursor = readable.query(name.name, null, null, null, null, null, null)

            val result = mutableListOf<DataItem>()

            with(cursor) {
                while (moveToNext()) {
                    val number = getString(getColumnIndexOrThrow(Entry.NUMBER))
                    val date = getLong(getColumnIndexOrThrow(Entry.DATE))
                    val blockType = getInt(getColumnIndexOrThrow(Entry.BLOCK_TYPE))

                    result.add(DataItem(number, Date(date), BlockType.fromInt(blockType)))
                }
            }

            cursor.close()

            return result
        }
}

object Entry : BaseColumns {
    const val NUMBER = "number"
    const val DATE = "date"
    const val BLOCK_TYPE = "block_type"
}

enum class TableName {
    NUMBERS,
    HISTORY
}

fun DataItem.values(): ContentValues {
    return ContentValues().apply {
        put(Entry.NUMBER, number)
        put(Entry.DATE, date.time)
        put(Entry.BLOCK_TYPE, blockType.rawValue)
    }
}