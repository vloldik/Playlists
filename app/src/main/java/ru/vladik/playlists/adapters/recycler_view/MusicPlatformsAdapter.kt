package ru.vladik.playlists.adapters.recycler_view

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import ru.vladik.playlists.R
import ru.vladik.playlists.dataclasses.musicservises.LastFmMusicService
import ru.vladik.playlists.dataclasses.musicservises.MusicService
import ru.vladik.playlists.utils.AppServices
import ru.vladik.playlists.utils.VColorUtils
import ru.vladik.playlists.utils.LoginUtil

class MusicPlatformsAdapter : RecyclerView.Adapter<MusicPlatformsAdapter.ViewHolder>() {

    private lateinit var mContext: Context
    private lateinit var onUserChangedListener: LoginUtil.UserChangedListener
    private val serviceList = AppServices.getServicesList()

    init {
        serviceList.remove(LastFmMusicService)
    }

    data class ViewHolder(var itemView: View,
                          var titleTextView: TextView = itemView.findViewById(R.id.element_title),
                          var descriptionTextView: TextView = itemView.findViewById(R.id.element_description),
                          var imageView: ImageView = itemView.findViewById(R.id.element_image),
                          var mainLayout: ViewGroup = itemView.findViewById(R.id.element_main_layout))
        : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context

        onUserChangedListener = object : LoginUtil.UserChangedListener {
            override fun onUserChanged(service: MusicService) {
                notifyItemChanged(AppServices.getServicesList().indexOf(service))
            }
        }
        LoginUtil.addUserChangedListener(onUserChangedListener)

        val view: ViewGroup = LayoutInflater.from(mContext).inflate(R.layout.rounded_element_with_image,
            parent, false) as ViewGroup
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val platform = serviceList[position]
        platform.checkUserRegistration(mContext)
        val platformImage = platform.imageRes?.let { AppCompatResources.getDrawable(mContext, it) }
        holder.titleTextView.text = platform.name
        val drawable: GradientDrawable =
            (holder.mainLayout.background as GradientDrawable).constantState?.newDrawable()
                ?.mutate() as GradientDrawable
        val drawableBitmap: Bitmap? = platformImage?.toBitmap(20, 20, Bitmap.Config.RGBA_F16)
        if (drawableBitmap != null) {
            val colorPrimary = VColorUtils.getAverageColor(drawableBitmap)
            drawable.colors = arrayOf(colorPrimary, colorPrimary).toIntArray()
        }
        holder.mainLayout.background = drawable
        holder.descriptionTextView.setTextColor(platform.textColor)
        holder.titleTextView.setTextColor(platform.textColor)
        if (platform.loggedIn) {
            val text =
                mContext.getText(R.string.you_logged_in_as).toString() + " " + platform.userName
            holder.descriptionTextView.text = text
            holder.mainLayout.setOnClickListener {
                val context = it.context
                val dialogBuilder = AlertDialog.Builder(context)
                dialogBuilder.setTitle(R.string.confirming)
                dialogBuilder.setMessage(R.string.are_you_sure_to_live)
                dialogBuilder.setPositiveButton(R.string.yes) { _, _ ->
                    LoginUtil.logOut(platform, mContext)
                }
                dialogBuilder.setNegativeButton(R.string.no) { dialog, _ ->
                    dialog.dismiss()
                }
                dialogBuilder.show()
            }
        } else {
            holder.descriptionTextView.text = mContext.getText(R.string.log_in)
            holder.mainLayout.setOnClickListener {
                LoginUtil.startAuthActivity(platform, mContext)
            }
        }

        holder.imageView.setImageDrawable(platformImage)
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        LoginUtil.removeUserChangedListener(onUserChangedListener)
    }
}