package com.pr0p1k.bcomp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
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
    private lateinit var background: DrawLayout
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

        for (i in 0..2047) memoryRows.add(Utils.toHex(0, 16))
        setSupportActionBar(toolbar)
        background.viewTreeObserver.addOnGlobalLayoutListener {
            initBuses()
        }
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
        var rects: kotlin.Array<Any>
        loop@ for (signal in ControlSignal.values()) {
            when (signal) {
                ControlSignal.MEMORY_WRITE -> {
                    rects = arrayOf(Rect(memory_view.x.toInt() + memory_view.width + 10,
                            address_register.y.toInt() - 10, address_register.x.toInt(),
                            address_register.y.toInt() + 10),
                            Rect(memory_view.x.toInt() + memory_view.width + 10,
                                    data_label.y.toInt() + 10, address_register.x.toInt(),
                                    data_label.y.toInt() + 30),
                            createArrow(memoryView.x + memoryView.width, address_register.y, 4),
                            createArrow(memoryView.x + memoryView.width, data_label.y + 20, 4))
                }

                ControlSignal.MEMORY_READ -> {
                    rects = arrayOf(Rect(memory_view.x.toInt() + memory_view.width + 10, address_register.y.toInt() - 10, address_register.x.toInt(), address_register.y.toInt() + 10),
                            Rect(memory_view.x.toInt() + memory_view.width, data_register.y.toInt() + 10, address_register.x.toInt() - 10, data_register.y.toInt() + 30),
                            createArrow(data_register.x, data_register.y + 20, 2),
                            createArrow(memoryView.x + memoryView.width, address_register.y, 4))
                }
                ControlSignal.DATA_TO_ALU -> {
                    val left = (data_register.x + memoryView.x + memoryView.width).toInt() / 2
                    val top2 = memoryView.y.toInt() + memoryView.height + 20
                    rects = arrayOf(Rect(left, data_label.y.toInt() + 10, data_register.x.toInt(), data_label.y.toInt() + 30),
                            Rect(left, data_label.y.toInt() + 10, left + 20, top2 + 20),
                            Rect(alu_image.x.toInt() + alu_image.width / 2 + 20, top2, left, top2 + 20),
                            Rect(alu_image.x.toInt() + alu_image.width / 2 + 20, top2, alu_image.x.toInt() + alu_image.width / 2 + 40, alu_label.y.toInt()))
                }
                ControlSignal.KEY_TO_ALU -> {
                    val top = accumulator.y.toInt() - 10
                    val top2 = memoryView.y.toInt() + memoryView.height + 20
                    rects = arrayOf(Rect(key_label.x.toInt() + key_label.width - 30, key_label.y.toInt(), key_label.x.toInt() + key_label.width - 10, accumulator.y.toInt() + accumulator.height + 80),
                            Rect(alu_image.x.toInt() - 40, accumulator.y.toInt() + accumulator.height + 60, key_label.x.toInt() + key_label.width - 10, accumulator.y.toInt() + accumulator.height + 80),
                            Rect(alu_image.x.toInt() - 40, top2, alu_image.x.toInt() - 20, top + accumulator.height + 90),
                            Rect(alu_image.x.toInt() - 40, top2, alu_label.x.toInt(), top2 + 20),
                            Rect(alu_label.x.toInt() - 20, top2, alu_label.x.toInt(), alu_label.y.toInt()))
                }
                ControlSignal.ACCUM_TO_ALU -> {
                    val left = carry.x.toInt() - 60
                    val top = accumulator.y.toInt() - 10
                    val top2 = memoryView.y.toInt() + memoryView.height + 20
                    rects = arrayOf(Rect(left, top, carry.x.toInt(), top + 20), // horizontal
                            Rect(left, top, left + 20, top + accumulator.height + 90), // vertical
                            Rect(alu_image.x.toInt() - 40, top + accumulator.height + 70, left, top + accumulator.height + 90), // horizontal
                            Rect(alu_image.x.toInt() - 40, top2, alu_image.x.toInt() - 20, top + accumulator.height + 90),
                            Rect(alu_image.x.toInt() - 40, top2, alu_label.x.toInt(), top2 + 20),
                            Rect(alu_label.x.toInt() - 20, top2, alu_label.x.toInt(), alu_label.y.toInt())) // vertical
                }
                ControlSignal.BUF_TO_IP -> {
                    val right = carry.x.toInt() - 100
                    val top = (accumulator.y + program_register.y).toInt() / 2 - 20
                    val top2 = accumulator.y.toInt() + accumulator.height + 20
                    val right2 = program_register.x.toInt() + program_register.width + 40
                    rects = arrayOf(Rect(alu_label.x.toInt(), alu_label.y.toInt(), alu_label.x.toInt() + 20, top2),
                            Rect(alu_label.x.toInt(), top2, right, top2 + 20),
                            Rect(right, top, right + 20, top2 + 20),
                            Rect(right, top, right2, top + 20),
                            Rect(right2, program_register.y.toInt() - 10, right2 + 20, top + 20),
                            Rect(right2 - 40, program_register.y.toInt() - 10, right2, program_register.y.toInt() + 10))
                }
                ControlSignal.BUF_TO_ACCUM -> {
                    val right = carry.x.toInt() - 100
                    val top = (accumulator.y + program_register.y).toInt() / 2 - 20
                    val top2 = accumulator.y.toInt() + accumulator.height + 20
                    val right2 = program_register.x.toInt() + 40
                    rects = arrayOf(Rect(alu_label.x.toInt(), alu_label.y.toInt(), alu_label.x.toInt() + 20, top2),
                            Rect(alu_label.x.toInt(), top2, right, top2 + 20),
                            Rect(right, top, right + 20, top2 + 20),
                            Rect(right, top, right2, top + 20),
                            Rect(right2, top, right2 + 20, acc_label.y.toInt()))
                }
                ControlSignal.BUF_TO_DATA -> {
                    val right = carry.x.toInt() - 100
                    val top = (accumulator.y + program_register.y).toInt() / 2 - 20
                    val top2 = accumulator.y.toInt() + accumulator.height + 20
                    val right2 = program_register.x.toInt() + program_register.width + 40
                    val right3 = data_register.x.toInt() + data_register.width + 40
                    rects = arrayOf(Rect(alu_label.x.toInt(), alu_label.y.toInt(), alu_label.x.toInt() + 20, top2),
                            Rect(alu_label.x.toInt(), top2, right, top2 + 20),
                            Rect(right, top, right + 20, top2 + 20),
                            Rect(right, top, right2, top + 20),
                            Rect(right2, program_register.y.toInt() - 10, right3, program_register.y.toInt() + 10),
                            Rect(right3, data_register.y.toInt() - 10, right3 + 20, program_register.y.toInt() + 10),
                            Rect(right2, program_register.y.toInt() - 10, right2 + 20, top + 20),
                            Rect(right2 - 40, data_register.y.toInt() - 10, right2, data_register.y.toInt() + 10),
                            Rect(right3 - 40, data_register.y.toInt() - 10, right3, data_register.y.toInt() + 10))
                }
                ControlSignal.BUF_TO_INSTR -> {
                    val right = carry.x.toInt() - 100
                    val top = (accumulator.y + program_register.y).toInt() / 2 - 20
                    val top2 = accumulator.y.toInt() + accumulator.height + 20
                    val right2 = program_register.x.toInt() + program_register.width + 40
                    val right3 = command_register.x.toInt() + command_register.width + 40
                    rects = arrayOf(Rect(alu_label.x.toInt(), alu_label.y.toInt(), alu_label.x.toInt() + 20, top2),
                            Rect(alu_label.x.toInt(), top2, right, top2 + 20),
                            Rect(right, top, right + 20, top2 + 20),
                            Rect(right, top, right2, top + 20),
                            Rect(right2, program_register.y.toInt() - 10, right2 + 20, top + 20),
                            Rect(right2, program_register.y.toInt() - 10, right3, program_register.y.toInt() + 10),
                            Rect(right3, command_register.y.toInt() - 10, right3 + 20, program_register.y.toInt() + 10),
                            Rect(right2 - 40, command_register.y.toInt() - 10, right2, command_register.y.toInt() + 10),
                            Rect(right3 - 40, command_register.y.toInt() - 10, right3, command_register.y.toInt() + 10))
                }
                ControlSignal.INSTR_TO_ALU -> {
                    val left = (data_register.x + memoryView.x + memoryView.width).toInt() / 2
                    val top2 = memoryView.y.toInt() + memoryView.height + 20
                    rects = arrayOf(Rect(left, command_register.y.toInt() - 10, command_register.x.toInt(), command_register.y.toInt() + 10),
                            Rect(left, command_register.y.toInt(), left + 20, top2 + 20),
                            Rect(alu_image.x.toInt() + alu_image.width / 2 + 20, top2, left, top2 + 20),
                            Rect(alu_image.x.toInt() + alu_image.width / 2 + 20, top2, alu_image.x.toInt() + alu_image.width / 2 + 40, alu_label.y.toInt()))
                }
                ControlSignal.IP_TO_ALU -> {
                    val left = alu_image.x.toInt() + alu_image.width / 2 + 20
                    val top2 = memoryView.y.toInt() + memoryView.height + 20
                    rects = arrayOf(Rect(left, top2, program_register.x.toInt(), top2 + 20),
                            Rect(left, top2, left + 20, alu_label.top))
                }
                ControlSignal.BUF_TO_ADDR -> {
                    val right = carry.x.toInt() - 100
                    val top = (accumulator.y + program_register.y).toInt() / 2 - 20
                    val top2 = accumulator.y.toInt() + accumulator.height + 20
                    val right2 = program_register.x.toInt() + program_register.width + 40
                    val right3 = data_register.x.toInt() + data_register.width + 40
                    rects = arrayOf(Rect(alu_label.x.toInt(), alu_label.y.toInt(), alu_label.x.toInt() + 20, top2),
                            Rect(alu_label.x.toInt(), top2, right, top2 + 20),
                            Rect(right, top, right + 20, top2 + 20),
                            Rect(right, top, right2, top + 20),
                            Rect(right2, program_register.y.toInt() - 10, right3, program_register.y.toInt() + 10),
                            Rect(right3, address_register.y.toInt() - 10, right3 + 20, program_register.y.toInt() + 10),
                            Rect(right2, program_register.y.toInt() - 10, right2 + 20, top + 20),
                            Rect(right2 - 40, address_register.y.toInt() - 10, right3, address_register.y.toInt() + 10))
                }
                else -> continue@loop
            }
            buses[signal] = BusView(rects, signal)
            bg.drawBuses(rects)
        }
    }

    /**
     * Creates triangle with direction.
     * directions: 1 - up, 2 - right, 3 - down, 4 - left
     */
    private fun createArrow(x: Float, y: Float, direction: Int): Path {
        var x1 = 0f;
        var y1 = 0f;
        var x2 = 0f;
        var y2 = 0f
        when (direction) {
            1 -> {
                x1 = x + 20
                y1 = (y + 40 / Math.sqrt(3.0)).toFloat()
                x2 = x - 20
                y2 = y1
            }
            2 -> {
                y1 = y + 20
                x1 = (x - 40 / Math.sqrt(3.0)).toFloat()
                y2 = y - 20
                x2 = x1
            }
            3 -> {
                x1 = x - 20
                y1 = (y - 40 / Math.sqrt(3.0)).toFloat()
                x2 = x + 20
                y2 = y1
            }
            4 -> {
                y1 = y - 20
                x1 = (x + 40 / Math.sqrt(3.0)).toFloat()
                y2 = y + 20
                x2 = x1
            }
        }
        val path = Path()
        path.fillType = Path.FillType.EVEN_ODD
        path.moveTo(x, y)
        path.lineTo(x1, y1)
        path.lineTo(x2, y2)
        path.lineTo(x, y)
        path.close()
        return path
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
        initBuses()
    }

    override fun stepStart() {
        if (app.openBuses.contains(ControlSignal.MEMORY_WRITE)) memTouched = true
        drawOpenBuses(false)
    }

    private fun drawOpenBuses(active: Boolean = true) {
        for (bus in buses)
            background.drawBuses(bus.value.rects)
        for (bus in app.openBuses)
            background.drawBuses(buses[bus]?.rects ?: emptyArray(), active)
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
