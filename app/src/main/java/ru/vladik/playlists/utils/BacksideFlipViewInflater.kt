package ru.vladik.playlists.utils

import android.view.ViewGroup
import androidx.annotation.DrawableRes
import ru.vladik.playlists.R
import ru.vladik.playlists.views.BackSideActionsView

class BacksideFlipViewInflater private constructor(private val actionList: List<BackSideActionsView.Action>,
                                                   private val onBackPressed: () -> Unit,
                                                   private val parent: ViewGroup) {

    fun getInflatedView() {
        parent.findViewById<BackSideActionsView>(R.id.back_side_actions).apply {
            removeAllViews()
            addActions(actionList)
        }
    }

    class Builder(private var onBackPressed: () -> Unit,
                  private val parent: ViewGroup,
                  private val actionList: ArrayList<BackSideActionsView.Action> = ArrayList(), ) {

        fun addAction(title: String, @DrawableRes drawableRes: Int, onClick: () -> Unit) =
            addAction(BackSideActionsView.Action(title, drawableRes, onClick))

        private fun addAction(action: BackSideActionsView.Action): Builder {
            actionList.add(action)
            return this
        }

        fun build() : BacksideFlipViewInflater =
            BacksideFlipViewInflater(actionList, onBackPressed, parent)
    }
}