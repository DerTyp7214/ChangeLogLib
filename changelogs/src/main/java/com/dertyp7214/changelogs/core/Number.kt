package com.dertyp7214.changelogs.core

import android.content.Context
import android.util.DisplayMetrics
import kotlin.math.roundToInt

fun Number.dpToPx(context: Context): Float {
    return this.toFloat() * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Number.dpToPxRounded(context: Context): Int {
    return dpToPx(context).roundToInt()
}