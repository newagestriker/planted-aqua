package com.newage.aquapets.helpers

import android.view.View
import androidx.databinding.BindingAdapter

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("visibleGone")
    fun showHide(view: View, show: Boolean) {
        view.visibility = if (show) View.GONE else View.VISIBLE
    }

    @JvmStatic
    @BindingAdapter("visibleGoneCheckBox")
    fun showHideCheckBox(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("visibleIfTrue")
    fun showIfTrue(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }
}