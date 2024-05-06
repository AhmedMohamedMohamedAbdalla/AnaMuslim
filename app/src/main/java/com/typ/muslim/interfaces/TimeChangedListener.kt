package com.typ.muslim.interfaces

import com.typ.muslim.models.Timestamp

interface TimeChangedListener {
    fun onTimeChanged(now: Timestamp)
}
