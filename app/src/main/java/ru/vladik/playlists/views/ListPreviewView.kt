package ru.vladik.playlists.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.vladik.playlists.R
import ru.vladik.playlists.utils.LayoutHelper

class ListPreviewView : LinearLayout {
    val textView: TextView
    val recyclerView: RecyclerView
    private val loadingContainer: ViewGroup
    private val clickableContainer: ViewGroup

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        orientation = VERTICAL
        val view = LayoutInflater.from(context).inflate(R.layout.list_preview, this)
        textView = view.findViewById(R.id.text_view)
        recyclerView = view.findViewById(R.id.recycler_view)
        loadingContainer = view.findViewById(R.id.loading_container)
        clickableContainer = view.findViewById(R.id.clickable_container)
    }

    fun setOnHeaderClickListener(listener: OnClickListener) {
        clickableContainer.setOnClickListener(listener)
    }

    fun setLoading(isLoading: Boolean) {
        LayoutHelper.setLoading(loadingContainer, isLoading)
    }
}