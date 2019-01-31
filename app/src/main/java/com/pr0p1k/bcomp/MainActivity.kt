package com.pr0p1k.bcomp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import ru.ifmo.cs.bcomp.Utils

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var memoryView: RecyclerView
    private lateinit var memoryRows: ArrayList<String>
    private lateinit var memoryRowAdapter: MemoryRowAdapter
    private lateinit var memoryLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        memoryRows = ArrayList()
        memoryRowAdapter = MemoryRowAdapter(memoryRows)
        memoryLayoutManager = LinearLayoutManager(this)

        memoryView = findViewById<RecyclerView>(R.id.memory_view).apply {
            layoutManager = memoryLayoutManager
            adapter = memoryRowAdapter
        }

        for (i in 0..2047) memoryRows.add(Utils.toHex(i, 16))
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    fun openMenu(menuItem: MenuItem) {
        drawerLayout.openDrawer(Gravity.START)
    }

    fun invertRegister(register: View) {
        if ((register as TextView).text == "0")
            register.text = "1"
        else register.text = "0"
    }

    class MemoryRowAdapter(private val memoryRows: ArrayList<String>) : RecyclerView.Adapter<MemoryRowAdapter.MemoryRowHolder>() {

        class MemoryRowHolder(var layout: RelativeLayout) : RecyclerView.ViewHolder(layout)

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MemoryRowHolder {
            val rowNumber = TextView(parent?.context)
            rowNumber.setBackgroundResource(R.drawable.memory_row)
            rowNumber.minEms = 2
            rowNumber.setPadding(10, 0, 0, 0)

            val rowValue = TextView(parent?.context)

            val layout = RelativeLayout(parent?.context)
            layout.gravity = Gravity.START
            layout.addView(rowNumber)
            layout.addView(rowValue)
            (rowValue.layoutParams as RelativeLayout.LayoutParams).setMargins(120, 0, 0, 0)

            return MemoryRowHolder(layout)
        }

        override fun getItemCount() = memoryRows.size

        override fun onBindViewHolder(holder: MemoryRowHolder, position: Int) {
            (holder.layout.getChildAt(0) as TextView).text = Utils.toHex(position, 12)
            (holder.layout.getChildAt(1) as TextView).text = memoryRows[position]
        }
    }
}
