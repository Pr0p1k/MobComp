package com.pr0p1k.bcomp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import ru.ifmo.cs.bcomp.*
import java.lang.StringBuilder
import java.sql.Array
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : PanelActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var memoryView: RecyclerView
    private lateinit var memoryRows: ArrayList<String>
    private lateinit var memoryRowAdapter: MemoryRowAdapter
    private lateinit var memoryLayoutManager: LinearLayoutManager
    private lateinit var app: ComponentManager
    private var buses = HashMap<ControlSignal, List<ImageView>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        app = applicationContext as ComponentManager

        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        memoryRows = ArrayList()
        memoryRowAdapter = MemoryRowAdapter(memoryRows)
        memoryLayoutManager = LinearLayoutManager(this)

        memoryView = findViewById<RecyclerView>(R.id.memory_view).apply {
            layoutManager = memoryLayoutManager
            adapter = memoryRowAdapter
        }

        initBuses()

        for (i in 0..2047) memoryRows.add(Utils.toHex(0, 16))
        setSupportActionBar(toolbar)
    }

    private fun initBuses() {
        buses[ControlSignal.KEY_TO_ALU] = listOf(button_alu, left_alu)

        buses[ControlSignal.BUF_TO_ADDR] = listOf(from_alu,
                acc_pc, pc_data, data_command, to_address)

        buses[ControlSignal.BUF_TO_IP] = listOf(from_alu, acc_pc, to_pc)

        buses[ControlSignal.IP_TO_ALU] = listOf(from_pc, right_alu)

        buses[ControlSignal.INSTR_TO_ALU] = listOf(from_command, data_pc, right_alu)
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

    fun inputAddress(button: View) {
        app.cmdEnterAddr()
    }


    fun start(button: View) {
        app.start()
    }

    fun continuation(button: View) {
        app.continuation()
    }

    fun halt(button: View) {
        app.halt()
    }

    override fun stepStart() {
        drawOpenBuses(false)
    }

    private fun drawOpenBuses(active: Boolean = true) {
        for (bus in app.openBuses) {
            if (active) {
                for (image in buses[bus] ?: emptyList()) {
                    DrawableCompat.setTint(
                            image.drawable, ContextCompat.getColor(this, R.color.colorRed))
                }
            } else
                for (images in buses.values)
                    for (image in images) {
                        DrawableCompat.setTint(
                                image.drawable, ContextCompat.getColor(this, R.color.busColor))
                    }

        }
    }

    override fun stepFinish() {
        drawOpenBuses()
    }

    fun tact(button: View) {
        val state = app.invertClockState()
        if (state)
            button.background = ContextCompat.getDrawable(this, R.drawable.just_button)
        else
            button.background = ContextCompat.getDrawable(this, R.drawable.active_button)
    }

    fun readKeyRegister(): String = StringBuilder().append(getBit(R.id.register15)).append(
            getBit(R.id.register14)).append(getBit(R.id.register13)).append(getBit(R.id.register12))
            .append(getBit(R.id.register11)).append(getBit(R.id.register10)).append(getBit(R.id.register9))
            .append(getBit(R.id.register8)).append(getBit(R.id.register7)).append(getBit(R.id.register6))
            .append(getBit(R.id.register5)).append(getBit(R.id.register4)).append(getBit(R.id.register3))
            .append(getBit(R.id.register2)).append(getBit(R.id.register1)).append(getBit(R.id.register0)).toString()

    fun getBit(id: Int) = findViewById<TextView>(id).text[0]

    override fun onResume() {
        super.onResume()
        app.currentActivity = this
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
            val width = parent?.findViewById<RecyclerView>(R.id.memory_view)?.width ?: 200
            Log.i("Width: ", "Width = $width")
            (rowValue.layoutParams as RelativeLayout.LayoutParams).setMargins(width / 2, 0, 0, 0)

            return MemoryRowHolder(layout)
        }

        override fun getItemCount() = memoryRows.size

        override fun onBindViewHolder(holder: MemoryRowHolder, position: Int) {
            (holder.layout.getChildAt(0) as TextView).text = Utils.toHex(position, 12)
            (holder.layout.getChildAt(1) as TextView).text = memoryRows[position]
        }
    }
}
