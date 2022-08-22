package ru.vladik.playlists.dialogs

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import ru.vladik.playlists.R
import ru.vladik.playlists.adapters.PlatformSpinnerAdapter

class FilterDialog(context: Context) : Dialog(context) {
    var callback: Callback? = null

    private val platformPicker: Spinner

    init {
        setContentView(R.layout.filter_popup_window)
        platformPicker = findViewById(R.id.platform_spinner)
        platformPicker.adapter = PlatformSpinnerAdapter(context)
        platformPicker.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                callback?.onPlatformChange(id)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    interface Callback {
        fun onPlatformChange(platformId: Long)
    }
}