package ru.vladik.playlists.views

import android.app.Dialog
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.PopupWindow
import androidx.core.view.doOnLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.vladik.playlists.R
import ru.vladik.playlists.dialogs.FilterDialog

class FilterActionButton : FloatingActionButton {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    private val dialog = FilterDialog(context)

    var filterCallback: FilterDialog.Callback? = null
        set(value) {
            field = value
            dialog.callback = filterCallback
        }

    init {
        setImageResource(R.drawable.settings)
        setOnClickListener { showFilterWindow() }
    }

    private fun showFilterWindow() {
        dialog.show()
    }
}