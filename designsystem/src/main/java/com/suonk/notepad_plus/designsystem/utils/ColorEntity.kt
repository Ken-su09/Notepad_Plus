package com.suonk.notepad_plus.designsystem.utils

import android.graphics.Color

enum class ColorEntity {
    PINK,
    PURPLE,
    GREEN,
    BLUE,
    ORANGE,
    YELLOW,
    RED,
}


fun ColorEntity.toARGB(): Int {
    return when (this) {
        ColorEntity.PINK -> Color.parseColor("#FF7fdeea")
        ColorEntity.PURPLE -> Color.parseColor("#FFd095db")
        ColorEntity.GREEN -> Color.parseColor("#FF7fdeea")
        ColorEntity.BLUE ->Color.parseColor("#FF7fdeea")
        ColorEntity.ORANGE -> Color.parseColor("#FF7fdeea")
        ColorEntity.YELLOW -> Color.parseColor("#FF7fdeea")
        ColorEntity.RED -> Color.parseColor("#FFFF3633")
    }
}