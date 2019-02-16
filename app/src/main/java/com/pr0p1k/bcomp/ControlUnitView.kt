package com.pr0p1k.bcomp

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.widget.TextView
import ru.ifmo.cs.bcomp.RunningCycle
import java.util.*

class ControlUnitView(context: Context, val states: EnumMap<RunningCycle, TextView>) : TextView(context) {
    fun setRunningCycle(cycle: RunningCycle) {
        cleanRunningCycle()
        if (cycle != RunningCycle.NONE)
            states[cycle]!!.setTextColor(resources.getColor(R.color.colorRed))
    }

    private fun cleanRunningCycle() {
        for (state in states) {
            state.value.setTextColor(resources.getColor(android.R.color.darker_gray))
        }
    }
}
