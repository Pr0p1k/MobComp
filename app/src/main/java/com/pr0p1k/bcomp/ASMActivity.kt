package com.pr0p1k.bcomp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_asm.*
import ru.ifmo.cs.bcomp.Assembler
import ru.ifmo.cs.bcomp.CPU

class ASMActivity : PanelActivity() {

    private lateinit var app: ComponentManager
    private lateinit var cpu: CPU
    private lateinit var asm: Assembler
    override var mem: MemoryView
        get() = MemoryView(arrayListOf())
        set(value) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app = applicationContext as ComponentManager
        app.currentActivity = this
        app.init()
        cpu = app.cpu
        asm = Assembler(cpu.instructionSet)

        setContentView(R.layout.activity_asm)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun stepStart() {
    }

    override fun stepFinish() {
    }

    override fun getRegisterViews(): Map<CPU.Reg, TextView> {
        return emptyMap()
    }

    override fun updateView() {
    }

    override fun onResume() {
        super.onResume()
        app.currentActivity = this
    }

    fun compile(button: View) {
        if (cpu.isRunning()) {
            errorLabel.text = "Для компиляции остановите выполняющуюся программу"
            return
        }

        app.saveDelay()
        val clock = cpu.clockState
        cpu.clockState = true

        try {
            asm.compileProgram(asm_code.text.toString())
            asm.loadProgram(cpu)
            errorLabel.text = ""
        } catch (ex: Exception) {
            errorLabel.text = ex.message
        }


        cpu.clockState = clock
        app.clearActiveSignals()
        app.restoreDelay()
    }

    fun openMenu(menuItem: MenuItem) {
        drawer_layout.openDrawer(Gravity.START)
    }

    fun faster(button: View) {
        app.setSpeed(true)
    }

    fun slower(button: View) {
        app.setSpeed(false)
    }

    fun toBasic(button: View) {
        val intent = Intent(this, MainActivity::class.java)
        // TODO save current asm code
        startActivity(intent)
    }

    fun toAsm(button: View) {
        // do nothing lol
    }
}
