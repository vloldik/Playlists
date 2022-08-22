package ru.vladik.playlists.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import androidx.core.graphics.ColorUtils
import ru.vladik.playlists.R
import ru.vladik.playlists.utils.LayoutHelper
import kotlin.math.max

class BackSideActionsView : GridLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun addActions(actionList: List<Action>) {
        actionList.forEach {addAction(it)}
    }

    init {
        useDefaultMargins = false
    }

    @Suppress
    fun addAction(action: Action) {
        val button = CardView(context).apply {
            val iconSize = LayoutHelper.dipToPx(30F, context).toInt()
            val dip1 = LayoutHelper.dipToPx(1F, context).toInt()
            val dip5 = LayoutHelper.dipToPx(5f, context).toInt()
            radius = dip5.toFloat()
            val d1 = GradientDrawable().apply {
                setStroke(dip1, ColorUtils.setAlphaComponent(Color.WHITE, 0x70))
            }
            val t = context.obtainStyledAttributes(intArrayOf(R.attr.selectableItemBackground))
            val d0 = t.getDrawable(0)
            t.recycle()
            background = LayerDrawable(arrayOf(d0, d1))
            setOnClickListener { action.action() }

            addView(ImageView(context).apply {
                setImageResource(action.drawableRes)
                imageTintList = ColorStateList.valueOf(Color.WHITE)
                layoutParams = FrameLayout.LayoutParams(iconSize, iconSize).apply {
                    gravity = Gravity.CENTER
                }
            })
            val lp = LayoutParams()
                .apply {
                    columnSpec = spec(UNDEFINED, FILL,1f)
                    rowSpec = spec(UNDEFINED, FILL,1f)
                    width = 0
                    height = 0
                }
            layoutParams = lp
        }
        addView(button)
        columnCount = max(1, childCount/2)
    }

    class Action(var title: String, @DrawableRes var drawableRes: Int, val action: () -> Unit)
}