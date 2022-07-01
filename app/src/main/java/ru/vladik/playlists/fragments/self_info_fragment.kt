package ru.vladik.playlists.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ru.vladik.playlists.R
import ru.vladik.playlists.adapters.view_pager.ViewPagerAdapter
import ru.vladik.playlists.utils.AppServices

class self_info_fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_self_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager)
        val tabs: TabLayout = view.findViewById(R.id.navigation_tabs)
        if (activity != null) {
            viewPager.adapter = ViewPagerAdapter(activity!!)
            TabLayoutMediator(tabs, viewPager) { tab: TabLayout.Tab, pos: Int ->
                tab.text = AppServices.getServicesList()[pos].name
            }.attach()
        }
    }
}