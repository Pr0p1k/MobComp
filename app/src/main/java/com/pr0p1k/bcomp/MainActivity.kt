package com.pr0p1k.bcomp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
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
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.thread

class MainActivity : PanelActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var memoryView: RecyclerView
    private lateinit var memoryRows: ArrayList<CharSequence>
    private lateinit var memoryRowAdapter: MemoryRowAdapter
    private lateinit var memoryLayoutManager: LinearLayoutManager
    private lateinit var app: ComponentManager
    private var buses = EnumMap<ControlSignal, BusView>(ControlSignal::class.java)
    private lateinit var uiHandler: Handler
    override lateinit var mem: MemoryView
    private var memTouched = false
    private lateinit var background: DrawView
    private lateinit var controlUnit: ControlUnitView
    private lateinit var busPaint: Paint
    private lateinit var activeBusPaint: Paint

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        drawerLayout = drawer_layout
        memoryRows = ArrayList()
        memoryRowAdapter = MemoryRowAdapter(memoryRows)
        memoryLayoutManager = LinearLayoutManager(this)
        background = bg
        initControlUnit()

        memoryView = memory_view.apply {
            layoutManager = memoryLayoutManager
            adapter = memoryRowAdapter
        }
        mem = MemoryView(memoryRows)

        app = applicationContext as ComponentManager
        app.currentActivity = this
        app.init()

        uiHandler = Handler {
            if (it.what == 0)
                app.updateView(memTouched)
            if (memTouched) memoryRowAdapter.notifyItemChanged(app.regs[CPU.Reg.ADDR]?.reg?.value ?: 0)
            memTouched = false
            controlUnit.setRunningCycle(app.cpu.runningCycle)
            return@Handler true
        }

        initBuses()

        for (i in 0..2047) memoryRows.add(Utils.toHex(0, 16))
        setSupportActionBar(toolbar)
        busPaint = Paint()
        busPaint.color = resources.getColor(R.color.busColor)
        val canvas = Canvas()
        canvas.drawRect(memoryView.x + memoryView.width, memoryView.y + 200,
                address_register.x, memoryView.y + 230, busPaint)
        background.draw(canvas)
        background.invalidate()
    }

    private fun initControlUnit() {
        val map = EnumMap<RunningCycle, TextView>(RunningCycle::class.java)
        for (value in RunningCycle.values()) {
            when (value) {
                RunningCycle.INSTR_FETCH -> map[value] = command_select
                RunningCycle.ADDR_FETCH -> map[value] = address_select
                RunningCycle.EXECUTION -> map[value] = execution
                RunningCycle.INTERRUPT -> map[value] = interrupt
                RunningCycle.PANEL -> map[value] = console_operation
                RunningCycle.NONE -> map[value] = program
            }
        }
        controlUnit = ControlUnitView(this, map)
    }

    private fun initBuses() {
        // TODO calculate the coords via registers
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
        app.updateKeyReg()
    }

    fun inputAddress(button: View) {
        app.cmdEnterAddr()
    }

    override fun getRegisterViews(): Map<CPU.Reg, TextView> {
        val map = HashMap<CPU.Reg, TextView>()
        for (reg in CPU.Reg.values()) {
            when (reg) {
                CPU.Reg.KEY -> {
                    map[reg] = KeyRegisterView(this, getKeyBits())
                }
                CPU.Reg.STATE -> {
                }
                CPU.Reg.ACCUM -> map[reg] = accumulator
                CPU.Reg.ADDR -> map[reg] = address_register
                CPU.Reg.DATA -> map[reg] = data_register
                CPU.Reg.INSTR -> map[reg] = command_register
                CPU.Reg.IP -> map[reg] = program_register
            }
        }
        return map
    }

    private fun getKeyBits(): List<TextView> {
        return listOf(register15, register14, register13, register12,
                register11, register10, register9, register8,
                register7, register6, register5, register4,
                register3, register2, register1, register0)
    }


    fun start(button: View) {
        app.start()
    }

    fun continuation(button: View) {
        app.continuation()
    }

    fun write(button: View) {
        app.cmdWrite()
    }

    fun halt(button: View) {
        app.halt()
    }

    override fun stepStart() {
        if (app.openBuses.contains(ControlSignal.MEMORY_WRITE)) memTouched = true
        drawOpenBuses(false)
    }

    private fun drawOpenBuses(active: Boolean = true) {
//        for (bus in app.openBuses) {
//            if (active) {
//                for (image in buses[bus] ?: emptyList()) {
//                    DrawableCompat.setTint(
//                            image.drawable, ContextCompat.getColor(this, R.color.colorRed))
//                }
//            } else
//                for (images in buses.values)
//                    for (image in images) {
//                        DrawableCompat.setTint(
//                                image.drawable, ContextCompat.getColor(this, R.color.busColor))
//                    }
//
//        }
        // TODO
    }

    override fun stepFinish() {
        drawOpenBuses()
        updateView()
    }

    /**
     * Updates all the registers after a step
     */
    override fun updateView() {
        uiHandler.sendEmptyMessage(0)
    }

    fun faster(button: View) {
        app.setSpeed(true)
    }

    fun slower(button: View) {
        app.setSpeed(false)
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

    class MemoryRowAdapter(private val memoryRows: ArrayList<CharSequence>) : RecyclerView.Adapter<MemoryRowAdapter.MemoryRowHolder>() {

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
