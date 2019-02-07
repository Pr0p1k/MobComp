package com.pr0p1k.bcomp

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.widget.Button
import ru.ifmo.cs.bcomp.BasicComp
import ru.ifmo.cs.bcomp.CPU
import ru.ifmo.cs.bcomp.ControlSignal
import ru.ifmo.cs.bcomp.MicroPrograms
import ru.ifmo.cs.elements.DataDestination

class ComponentManager : Application {
    private val bcomp = BasicComp(MicroPrograms.getMicroProgram(MicroPrograms.DEFAULT_MICROPROGRAM))
    private val cpu = bcomp.cpu
    private var cuswitch = false
    val openBuses = ArrayList<ControlSignal>()
    lateinit var currentActivity: PanelActivity

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
            currentActivity.stepStart()
            openBuses.clear()
        }
        cpu.setTickFinishListener {
            currentActivity.stepFinish()
            // some more shit here
        }

        for (cs in busSignals)
            bcomp.addDestination(cs, SignalHandler(cs))
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
            // set value
        } else {
            cpu.startSetAddr()

        }
    }

    inner class SignalHandler(private val signal: ControlSignal) : DataDestination {

        override fun setValue(value: Int) {
            openBuses.add(signal)
        }
    }
}
