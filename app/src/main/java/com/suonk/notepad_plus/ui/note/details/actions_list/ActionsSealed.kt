package com.suonk.notepad_plus.ui.note.details.actions_list

import androidx.annotation.DrawableRes
import com.suonk.notepad_plus.R

enum class ActionsSealed(
    @DrawableRes
    val icon: Int,
    @DrawableRes
    var background: Int,
    val type: ActionType
) {
    UNDO(R.drawable.ic_undo, R.drawable.custom_item_background, ActionType.REVERSE),
    REDO(R.drawable.ic_redo, R.drawable.custom_item_background, ActionType.REVERSE),

    BOLD(R.drawable.ic_bold, R.drawable.custom_item_background, ActionType.TEXT_STYLE),
    ITALIC(R.drawable.ic_italic, R.drawable.custom_item_background, ActionType.TEXT_STYLE),

    SUBSCRIPT(R.drawable.ic_x2_subscript, R.drawable.custom_item_background, ActionType.X2),
    SQUARED(R.drawable.ic_x2_squared, R.drawable.custom_item_background, ActionType.X2),

    H1(R.drawable.ic_heading_1, R.drawable.custom_item_background, ActionType.HEADING),
    H2(R.drawable.ic_heading_2, R.drawable.custom_item_background, ActionType.HEADING),
    H3(R.drawable.ic_heading_3, R.drawable.custom_item_background, ActionType.HEADING),
    H4(R.drawable.ic_heading_4, R.drawable.custom_item_background, ActionType.HEADING),
    H5(R.drawable.ic_heading_5, R.drawable.custom_item_background, ActionType.HEADING),
    H6(R.drawable.ic_heading_6, R.drawable.custom_item_background, ActionType.HEADING),

    TEXT_COLOR(R.drawable.ic_text_color, R.drawable.custom_item_background, ActionType.COLOR),
    BACKGROUND_COLOR(R.drawable.ic_background_color, R.drawable.custom_item_background, ActionType.COLOR),

    INSERT_IMAGE(R.drawable.ic_insert_image, R.drawable.custom_item_background, ActionType.INSERT),
    INSERT_LINK(R.drawable.ic_link, R.drawable.custom_item_background, ActionType.INSERT),

    LEFT_ALIGN(R.drawable.ic_left_align, R.drawable.custom_item_background, ActionType.ALIGN),
    RIGHT_ALIGN(R.drawable.ic_right_align, R.drawable.custom_item_background, ActionType.ALIGN),
    CENTER_ALIGN(R.drawable.ic_center_align, R.drawable.custom_item_background, ActionType.ALIGN)
}

enum class ActionType {
    REVERSE,
    TEXT_STYLE,
    X2,
    HEADING,
    COLOR,
    INSERT,
    ALIGN
}