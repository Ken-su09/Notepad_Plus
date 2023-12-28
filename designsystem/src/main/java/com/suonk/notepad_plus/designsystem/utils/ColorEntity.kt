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
        ColorEntity.PINK -> Color.parseColor("#FFFF80CF")
        ColorEntity.PURPLE -> Color.parseColor("#FFd095db")
        ColorEntity.GREEN -> Color.parseColor("#FF78E527")
        ColorEntity.BLUE ->Color.parseColor("#FF7fdeea")
        ColorEntity.ORANGE -> Color.parseColor("#FFFFB64C")
        ColorEntity.YELLOW -> Color.parseColor("#FFFFEC4C")
        ColorEntity.RED -> Color.parseColor("#FFFC8C8C")
    }
}