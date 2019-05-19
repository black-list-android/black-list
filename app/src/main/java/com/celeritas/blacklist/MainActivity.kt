package com.celeritas.blacklist

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import com.hbb20.CountryCodePicker

class MainActivity : AppCompatActivity() {

    private lateinit var inputField: EditText
    private lateinit var inputPanel: LinearLayout
    private lateinit var addButton: ImageButton
    private lateinit var numbers: DataSet
    private lateinit var history: DataSet
    private lateinit var numbersList: RecyclerView
    private lateinit var historyList: RecyclerView
    private lateinit var db: DbWrapper

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_numbers -> {
                numbersList.visibility = View.VISIBLE
                historyList.visibility = View.GONE
                inputPanel.visibility = View.VISIBLE
                //itemsList.adapter = ItemListAdapter(numbers, getString(R.string.subtitle_format_numbers))
                //itemsList.adapter?.notifyDataSetChanged()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_history -> {
                //itemsList.adapter = ItemListAdapter(history, getString(R.string.subtitle_format_history))
                //itemsList.adapter?.notifyDataSetChanged()
                //inputPanel.layoutParams.height = 0
                //inputPanel.requestLayout()
                numbersList.visibility = View.GONE
                historyList.visibility = View.VISIBLE
                inputPanel.visibility = View.GONE
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        db = DbWrapper(this.application)

        inputField = findViewById(R.id.numberInput)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        addButton = findViewById(R.id.addButton)
        addButton.isEnabled = false


        val countryPicker: CountryCodePicker = findViewById(R.id.countryCodePicker)
        countryPicker.registerCarrierNumberEditText(inputField)

        countryPicker.setPhoneNumberValidityChangeListener { isValid ->
            addButton.isEnabled = isValid
            val resource =
                if (isValid) R.drawable.ic_playlist_add_black_24dp else R.drawable.ic_not_interested_black_24dp
            addButton.setImageResource(resource)
        }

        numbers = DataSet(db.numbers)
        history = DataSet(db.history)

        numbersList = findViewById(R.id.numbersList)
        val adapter = ItemListAdapter(numbers, getString(R.string.subtitle_format_numbers))
        numbersList.adapter = adapter
        ItemTouchHelper(SwipeToDelete(adapter)).attachToRecyclerView(numbersList)

        historyList = findViewById(R.id.historyList)
        historyList.adapter = ItemListAdapter(history, getString(R.string.subtitle_format_history))
        historyList.visibility = View.GONE

        listOf(numbersList, historyList).forEach {
            it.layoutManager = LinearLayoutManager(this)
            it.setHasFixedSize(true)
        }

        addButton.setOnClickListener {
            if (countryPicker.isValidFullNumber) {
                val number = countryPicker.formattedFullNumber
                numbers.add(number, BlockType.ALL)
                numbersList.adapter?.notifyDataSetChanged()
                inputField.text = null
            }

            hideKeyboard()
        }

        inputPanel = findViewById(R.id.inputLayout)
    }

    private fun hideKeyboard() {
        val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(inputField.windowToken, 0)
    }
}
