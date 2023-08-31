package com.suonk.notepad_plus.utils

import android.content.Context
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

sealed class NativeText {
    data class Simple(val text: String) : NativeText()
    data class Resource(@StringRes val id: Int) : NativeText()
    data class Plural(@PluralsRes val id: Int, val number: Int, val args: List<Any>) : NativeText()
    data class Argument(@StringRes val id: Int, val arg: Any) : NativeText()
    data class Arguments(@StringRes val id: Int, val args: List<Any>) : NativeText()
    data class Multi(val text: List<NativeText>) : NativeText()
}

fun TextView.setText(nativeText: NativeText): CharSequence = nativeText.toCharSequence(context)

fun NativeText.toCharSequence(context: Context): CharSequence {
    return when (this) {
        is NativeText.Argument -> context.getString(id, arg)
        is NativeText.Arguments -> context.getString(id, *args.toTypedArray())
        is NativeText.Multi -> {
            val builder = StringBuilder()
            for (t in text) {
                builder.append(t.toCharSequence(context))
            }
            builder.toString()
        }
        is NativeText.Plural -> context.resources.getQuantityString(id, number, *args.toTypedArray())
        is NativeText.Resource -> context.getString(id)
        is NativeText.Simple -> text
    }
}

fun NativeText.showToast(context: Context): Toast = Toast.makeText(context, toCharSequence(context), Toast.LENGTH_LONG).also {
    it.show()
}
