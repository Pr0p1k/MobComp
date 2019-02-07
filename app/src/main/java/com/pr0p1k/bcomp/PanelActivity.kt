package com.pr0p1k.bcomp

import android.support.v7.app.AppCompatActivity

abstract class PanelActivity: AppCompatActivity() {

    abstract fun stepStart()

    abstract fun stepFinish()

}
