package com.celeritas.blacklist

import android.telephony.PhoneNumberUtils
import java.util.*

data class DataItem(
    val number: String,
    val date: Date,
    var hint: String
)

class DataSet(private val table: Table) {
    private val entries = table.items.toMutableList()

    fun add(number: String, hint: String = "", isUnique: Boolean = true): Int {
        val formatted = PhoneNumberUtils.formatNumber(number, Locale.getDefault().country)

        val existing = entries.indexOfFirst { it.number == formatted }

        if (existing >= 0 && isUnique) return existing

        val item = DataItem(formatted, Date(), hint)

        entries.add(item)

        table.add(item)

        return entries.size - 1
    }

    fun remove(at: Int) {
        val entry = entries[at]
        entries.removeAt(at)

        table.remove(entry.number)
    }

    fun updateHint(at: Int, with: String) {
        val item = items[at]
        item.hint = with

        table.update(item)
    }

    val items: List<DataItem>
        get() = entries
}
