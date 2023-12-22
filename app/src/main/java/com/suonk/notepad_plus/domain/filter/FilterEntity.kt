package com.suonk.notepad_plus.domain.filter

import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.designsystem.utils.ColorEntity

enum class FilterEntity(val isMatching: (NoteEntity) -> Boolean) {
    REMOVE_FILTER({ true }),
    PINK({ it.color == ColorEntity.PINK }),
    ORANGE({ it.color == ColorEntity.ORANGE }),
    PURPLE({ it.color == ColorEntity.PURPLE }),
    GREEN({ it.color == ColorEntity.GREEN }),
    BLUE({ it.color == ColorEntity.BLUE }),
    YELLOW({ it.color == ColorEntity.YELLOW }),
    RED({ it.color == ColorEntity.RED })
}