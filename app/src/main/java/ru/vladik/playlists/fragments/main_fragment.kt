package ru.vladik.playlists.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.vpaliy.last_fm_api.LastFmService
import ru.vladik.playlists.R
import ru.vladik.playlists.adapters.recycler_view.MusicPlatformsAdapter
import ru.vladik.playlists.utils.Constants


class main_fragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var lastFmService: LastFmService
    private lateinit var platformsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        platformsRecyclerView = view.findViewById(R.id.platforms_recycler_view)
        checkNotNull(context)
        platformsRecyclerView.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.HORIZONTAL, false)
        platformsRecyclerView.adapter = MusicPlatformsAdapter()
        Log.d("main", Constants.vkUser.toString())
    }


}