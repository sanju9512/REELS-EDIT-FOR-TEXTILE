package com.example.ui.components

import android.content.Context
import androidx.annotation.DrawableRes
import com.example.R

object ResourceUtils {
    @DrawableRes
    fun getDrawableId(context: Context, resName: String): Int {
        return when (resName) {
            "textile_banarasi_saree_1790074526981" -> R.drawable.textile_banarasi_saree_1790074526981
            "textile_designer_kurti_1790074540938" -> R.drawable.textile_designer_kurti_1790074540938
            else -> {
                val id = context.resources.getIdentifier(resName, "drawable", context.packageName)
                if (id != 0) id else R.drawable.textile_banarasi_saree_1790074526981
            }
        }
    }
}
