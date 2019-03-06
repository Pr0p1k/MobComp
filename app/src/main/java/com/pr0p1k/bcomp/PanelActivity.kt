package com.pr0p1k.bcomp

import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.MenuItem
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import ru.ifmo.cs.bcomp.CPU

abstract class PanelActivity : AppCompatActivity() {

    abstract var mem: MemoryView

    abstract fun stepStart()

    abstract fun stepFinish()

    abstract fun getRegisterViews(): Map<CPU.Reg, TextView>

    abstract fun updateView()
}
