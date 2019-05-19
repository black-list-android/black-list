package com.celeritas.blacklist

import android.telephony.PhoneNumberUtils
import java.util.*

data class DataItem(
    val number: String,
    val date: Date,
    var blockType: BlockType
)

class DataSet(private val table: Table) {
    private val entries = table.items.toMutableList()

    fun add(number: String, blockType: BlockType): Int {
        val formatted = PhoneNumberUtils.formatNumber(number, Locale.getDefault().country)

        val existing = entries.indexOfFirst { it.number == formatted }

        if (existing >= 0) return existing

        val item = DataItem(formatted, Date(), blockType)

        entries.add(item)

        table.add(item)

        return entries.size - 1
    }

    fun remove(at: Int) {
        val entry = entries[at]
        entries.removeAt(at)

        table.remove(entry.number)
    }

    fun updateBlockType(at: Int) {
        val item = items[at]
        item.blockType = nextBlockType(item.blockType)

        table.update(item)
    }

    val items: List<DataItem>
        get() = entries
}

enum class BlockType(val rawValue: Int) {
    SMS(0),
    NUMBER(1),
    ALL(2);

    companion object {
        fun fromInt(value: Int): BlockType {
            return values().first { it.rawValue == value }
        }
    }
}

private fun nextBlockType(current: BlockType): BlockType {
    return when (current) {
        BlockType.ALL -> BlockType.NUMBER
        BlockType.NUMBER -> BlockType.SMS
        BlockType.SMS -> BlockType.ALL
    }
}