package com.pr0p1k.bcomp

import android.app.Application
import android.support.annotation.IntegerRes
import android.util.Log
import android.widget.TextView
import ru.ifmo.cs.bcomp.*
import ru.ifmo.cs.elements.DataDestination
import ru.ifmo.cs.elements.DataWidth
import ru.ifmo.cs.elements.Memory
import ru.ifmo.cs.elements.Register
import java.util.*

class ComponentManager : Application {
    private val bcomp = BasicComp(MicroPrograms.getMicroProgram(MicroPrograms.DEFAULT_MICROPROGRAM))
    private val cpu = bcomp.cpu
    private var cuswitch = false
    val openBuses = ArrayList<ControlSignal>()
    var currentActivity: PanelActivity? = null
    val regs = EnumMap<CPU.Reg, RegisterView>(CPU.Reg::class.java)
    private val delayPeriods = longArrayOf(0, 1, 5, 10, 25, 50, 100, 1000)
    private var currentDelay = 3
    private var memoryView: MemoryView? = null

    companion object {
        @JvmStatic
        val busSignals = arrayOf(
                ControlSignal.DATA_TO_ALU, ControlSignal.IO1_TSF,
                ControlSignal.INSTR_TO_ALU, ControlSignal.IP_TO_ALU,
                ControlSignal.ACCUM_TO_ALU, ControlSignal.STATE_TO_ALU,
                ControlSignal.KEY_TO_ALU, ControlSignal.BUF_TO_ADDR,
                ControlSignal.BUF_TO_DATA, ControlSignal.BUF_TO_INSTR,
                ControlSignal.BUF_TO_IP, ControlSignal.BUF_TO_ACCUM,
                ControlSignal.MEMORY_READ, ControlSignal.MEMORY_WRITE,
                ControlSignal.INPUT_OUTPUT, ControlSignal.IO0_TSF,
                ControlSignal.IO1_OUT, ControlSignal.IO2_TSF,
                ControlSignal.IO2_IN, ControlSignal.IO3_IN,
                ControlSignal.IO3_OUT, ControlSignal.IO3_TSF)
    }

    constructor() : super() {

        cpu.setTickStartListener {
            currentActivity?.stepStart()
            openBuses.clear()
        }
        cpu.setTickFinishListener {
            currentActivity?.stepFinish()
            if (delayPeriods[currentDelay] != 0L)
                try {
                    Thread.sleep(delayPeriods[currentDelay])
                } catch (e: InterruptedException) {
                }
        }

        for (cs in busSignals)
            bcomp.addDestination(cs, SignalHandler(cs))


    }

    fun init() {
        val map = currentActivity?.getRegisterViews()
        memoryView = currentActivity?.mem
        for (reg in CPU.Reg.values()) {
            when (reg) {
                CPU.Reg.KEY -> {
                    regs[reg] = RegisterView(map!![reg], reg, cpu.getRegister(reg))
                }
                CPU.Reg.STATE -> {
                    // TODO some shit
                }
                else -> {
                    regs[reg] = RegisterView(map!![reg], reg, cpu.getRegister(reg))
                }
            }
        }
    }

    fun start() {
        cpu.startStart()
    }

    fun continuation() {
        cpu.startContinue()
    }

    fun halt() {
        // TODO idk
    }

    fun invertClockState(): Boolean {
        return cpu.invertClockState()
    }

    fun cmdEnterAddr() {
        if (cuswitch) {
            cpu.runSetMAddr()
            // TODO idk
        } else {
            cpu.startSetAddr()

        }
    }

    fun cmdWrite() {
        if (cuswitch) {
            cpu.runMWrite()
            // micromem update
            regs.get(CPU.Reg.MIP)?.setValue()
        } else cpu.startWrite()
    }

    /**
     * Updates all the registers after a step
     */
    fun updateView(updateMem: Boolean = false) {
        for (reg in regs) {
            reg.value.setValue()
        }
        if (updateMem) {
            val addr: Int = regs[CPU.Reg.ADDR]?.reg?.value ?: 0
            memoryView?.memoryRows!![addr] = Utils.toHex(regs[CPU.Reg.DATA]?.reg?.value?:0, 16) ?: "0000"
        }
    }

    fun updateKeyReg() {
        regs[CPU.Reg.KEY]?.reg?.value = Integer.parseInt(
                (regs[CPU.Reg.KEY]?.view as KeyRegisterView).getValue(), 2)
    }

    fun setSpeed(faster: Boolean) {
        currentDelay = if (!faster)
            if (currentDelay < delayPeriods.size - 1)
                currentDelay + 1
            else 0 else if (currentDelay > 0)
            currentDelay - 1 else delayPeriods.size - 1
    }

    inner class SignalHandler(private val signal: ControlSignal) : DataDestination {

        override fun setValue(value: Int) {
            openBuses.add(signal)
        }
    }

    inner class RegisterView(val view: TextView?, private val regType: CPU.Reg, val reg: Register) : DataDestination {
        private var formatWidth: Int = if (regType == CPU.Reg.IP || regType == CPU.Reg.ADDR) 11 else 16
        private var hex: Boolean = false
        private var valuemask: Int = 65535


        fun setValue() {
            val str = if (hex) Utils.toHex(reg.value and valuemask, formatWidth)
            else Utils.toBinary(reg.value and valuemask, formatWidth)
            setValue(str)
        }

        protected fun setProperties(x: Int, y: Int, hex: Boolean, regWidth: Int) {
            this.hex = hex
            this.formatWidth = regWidth
            this.valuemask = DataWidth.getMask(regWidth)

//            setBounds(x, y, getValueWidth(regWidth, hex))
//            setTitle(if (hex) reg.name else reg.fullname)
//            setValue()
//
//            value.setBounds(1, getValueY(), width - 2, CELL_HEIGHT)
        }

        fun setProperties(x: Int, y: Int, hex: Boolean) {
            setProperties(x, y, hex, reg.width)
        }


        override fun setValue(value: Int) {
            setValue()
        }

        fun setValue(value: String) {
            view?.text = value
        }

    }
}
