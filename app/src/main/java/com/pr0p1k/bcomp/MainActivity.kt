package com.pr0p1k.bcomp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var memoryView: RecyclerView
    private lateinit var memoryRows: ArrayList<String>
    private lateinit var memoryRowAdapter: MemoryRowAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        memoryRows = ArrayList()
        for (i in 0 .. 30) {
            memoryRows.add("Kek $i")
        }
        memoryRowAdapter = MemoryRowAdapter(memoryRows)

        memoryView = findViewById<RecyclerView>(R.id.memory_view).apply {
            adapter = memoryRowAdapter
        }

        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    fun openMenu(menuItem: MenuItem) {
        memoryRows.add("Lol ${memoryRows.size}")
        drawerLayout.openDrawer(Gravity.START)
    }

    class MemoryRowAdapter(private val memoryRows: ArrayList<String>) : RecyclerView.Adapter<MemoryRowAdapter.MemoryRowHolder>() {

        class MemoryRowHolder(var textView: TextView) : RecyclerView.ViewHolder(textView)

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MemoryRowHolder {
            return MemoryRowHolder(TextView(parent?.context))
        }

        override fun getItemCount() = memoryRows.size

        override fun onBindViewHolder(holder: MemoryRowHolder, position: Int) {
            holder.textView.text = memoryRows[position]
        }
    }
}
