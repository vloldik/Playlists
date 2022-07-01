package ru.vladik.playlists.adapters.view_pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import ru.vladik.playlists.fragments.playlist_list_fragment
import ru.vladik.playlists.fragments.self_info_fragment
import ru.vladik.playlists.utils.AppServices

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> playlist_list_fragment.getInstance(AppServices.vk)
            else -> Fragment()
        }
    }

    override fun getItemCount(): Int {
        return 1
    }
}