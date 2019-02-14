package com.pr0p1k.bcomp

import android.content.Context
import android.widget.TextView

class KeyRegisterView(context: Context, private val bits: List<TextView>) : TextView(context) {

    fun getValue(): String {
        val value = java.lang.StringBuilder()
        for (bit in bits) {
            value.append(bit.text)
        }
        return value.toString()
    }

}
