package com.example.myapplication

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection


/**
 * A simple [Fragment] subclass.
 * Use the [LibraryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LibraryFragment : Fragment() {
    private lateinit var darkButton: Button
    private lateinit var mediumButton: Button
    private lateinit var brightButton: Button
    private lateinit var playlistRecyclerView: RecyclerView

    private var currentSongList: List<CompactSong> = emptyList()


    data class CompactSong(
        val name: String,
        val artist: String,
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val accessToken = arguments?.getString("accessToken")
        val darkID = arguments?.getString("darkID")
        val brightID = arguments?.getString("brightID")
        val mediumID = arguments?.getString("mediumID")
        darkButton = view.findViewById<Button>(R.id.btn_dark)
        mediumButton = view.findViewById(R.id.btn_medium)
        brightButton = view.findViewById(R.id.btn_bright)
        playlistRecyclerView = view.findViewById(R.id.playlist_recycler_view)

        darkButton.setOnClickListener {
            val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            if (networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                loadPlaylist(darkID, accessToken)
            }else{
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }
        mediumButton.setOnClickListener {
            val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            if (networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                loadPlaylist(mediumID, accessToken)
            }else{
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            }
            //Log.i("pid", "hhhhhhh")
        }
        brightButton.setOnClickListener {
            val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            if (networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                loadPlaylist(brightID, accessToken)
            }else{
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun loadPlaylist(playlistId: String?, accessToken: String?) {
        // Retrieve playlists from Spotify web API
        //Log.i("pid", "$accessToken")
        //Log.i("aid", "$playlistId")
        if (accessToken != null && playlistId != null) {
            lifecycleScope.launch {
                val songs = getPlaylistSongs(accessToken, playlistId)
                currentSongList = songs
                val adapter = PlaylistAdapter(songs)
                playlistRecyclerView.adapter = adapter
                playlistRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    private suspend fun getPlaylistSongs(accessToken: String, playlistId: String): List<CompactSong> = withContext(
        Dispatchers.IO) {
        val playlistUrl = "https://api.spotify.com/v1/playlists/$playlistId/tracks"
        val url = URL(playlistUrl)
        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Authorization", "Bearer $accessToken")

        val responseCode = connection.responseCode
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonResponse = JSONObject(response)
            val tracks = jsonResponse.getJSONArray("items")
            val songList = mutableListOf<CompactSong>()
            //Log.i("psong", "reach1")

            for (i in 0 until tracks.length()) {
                val track = tracks.getJSONObject(i).getJSONObject("track")
                val songName = track.getString("name")
                val artistName = track.getJSONArray("artists").getJSONObject(0).getString("name")
                val song = CompactSong(
                    songName,
                    artistName
                )
                songList.add(song)
            }
            //Log.i("psong", "reach2")
            return@withContext songList
        } else {
            //Log.e("GetPlaylistSongs", "HTTP error code: $responseCode")
            return@withContext emptyList()
        }
    }

}