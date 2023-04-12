package com.example.myapplication

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection


/**
 * A simple [Fragment] subclass.
 * Use the [RecommendFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecommendFragment : Fragment() {

    private lateinit var mBtnCheckLight: Button
    private lateinit var mtvLightVal: TextView
    private lateinit var mtvSongName: TextView
    private lateinit var mtvArtistName: TextView
    private lateinit var mivAlbumPic: ImageView
    private lateinit var mBtnAdd : Button
    private lateinit var mBtnListen: Button
    private lateinit var mtvListenStatus: TextView

    private lateinit var recommendedTracks: List<Song>

    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    private var currentLightLevel: Float = 0f
    var playlistFlag=-1
    private var player: ExoPlayer? = null
    data class Song(
        val name: String,
        val uri: String,
        val artist: String,
        val albumImageUrl: String,
        val previewUrl: String,
        val songID: String
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_recommend, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val accessToken = arguments?.getString("accessToken")
        if (accessToken != null) {
            //Log.i("bundleCheck", accessToken)
        } else {
            //Log.i("bundleCheck", "accessToken is null")
        }
        var currentIndex = 0
        val darkID = arguments?.getString("darkID")
        val brightID = arguments?.getString("brightID")
        val mediumID = arguments?.getString("mediumID")

        val darkP : String? = arguments?.getString("dark")
        val mediumP : String? = arguments?.getString("medium")
        val brightP : String? = arguments?.getString("bright")
        //Log.i("myTag", brightP.toString())
        // Here you can access your views and add your logic
        mBtnCheckLight = view.findViewById<Button>(R.id.getSong)
        mtvSongName = view.findViewById(R.id.songName)
        mtvArtistName = view.findViewById(R.id.artist)
        mivAlbumPic = view.findViewById(R.id.albumPic)
        mBtnAdd = view.findViewById(R.id.btnAdd)
        mtvLightVal = view.findViewById<TextView>(R.id.lightVal)
        mBtnListen=view.findViewById<Button>(R.id.listenButton)
        mtvListenStatus=view.findViewById<TextView>(R.id.listenStatus)

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        recommendedTracks = emptyList()
        mBtnListen.setOnClickListener(){
            lifecycleScope.launch {
                val previewUrl = getPreviewUrl(recommendedTracks[currentIndex].songID, accessToken)
                //Log.i("preview", "$previewUrl")
                if(!previewUrl.equals("null")) {
                    player = ExoPlayer.Builder(requireContext()).build()
                    val dataSourceFactory = DefaultHttpDataSource.Factory()
                    val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(previewUrl ?: ""))
                    player?.setMediaSource(mediaSource)
                    player?.prepare()
                    player?.play()
                    mtvListenStatus.setText("Playing")

                }else{
                    mtvListenStatus.setText("No Existing Preview")
                }
            }
        }

        mBtnCheckLight.setOnClickListener {
            player?.release()
            mtvListenStatus.setText("")
            currentLightLevel = measureAmbientLight()
            //Log.i("myTag", currentLightLevel.toString())

            if (recommendedTracks.isEmpty()) {
                lifecycleScope.launch {
                    //Log.i("myTag","TEST")
                    if(currentLightLevel < 5){
                        //Log.i("myTag","Getting dark prefs")
                        recommendedTracks = getSongRecommendations(accessToken, darkP)
                        mtvLightVal.text = "Light Value: "+currentLightLevel.toString()+" Playlist: Dark"
                        playlistFlag=1
                    } else if(currentLightLevel >= 5 && currentLightLevel < 20){
                        //Log.i("myTag","Getting medium prefs")
                        recommendedTracks = getSongRecommendations(accessToken, mediumP)
                        mtvLightVal.text = "Light Value: "+currentLightLevel.toString()+" Playlist: Medium"
                        playlistFlag=2
                    } else {
                        //Log.i("myTag","Getting bright prefs")
                        recommendedTracks = getSongRecommendations(accessToken, brightP)
                        mtvLightVal.text = "Light Value: "+currentLightLevel.toString()+" Playlist: Bright"
                        playlistFlag=3
                    }

                    currentIndex = 0
                    if (recommendedTracks.isNotEmpty()) {
                        displaySong(currentIndex)
                        delay(1000)
                    }
                }
            } else {
                currentIndex++
                if(!recommendedTracks.isEmpty()) {
                    displaySong(currentIndex)
                }
            }
        }

        mBtnAdd.setOnClickListener {
            player?.release()
            mtvListenStatus.setText("")
            if(playlistFlag!=-1){
                //add recommendedTracks[currentIndex] to according playlist
                if (recommendedTracks.isEmpty()) {
                    lifecycleScope.launch {
                        //Log.i("myTag","TEST")
                        if(currentLightLevel < 5){
                            //Log.i("myTag","Getting dark prefs")
                            recommendedTracks = getSongRecommendations(accessToken, darkP)
                            mtvLightVal.text = "Light Value: "+currentLightLevel.toString()+" Playlist: Dark"
                            playlistFlag=1
                        } else if(currentLightLevel >= 5 && currentLightLevel < 20){
                            //Log.i("myTag","Getting medium prefs")
                            recommendedTracks = getSongRecommendations(accessToken, mediumP)
                            mtvLightVal.text = "Light Value: "+currentLightLevel.toString()+" Playlist: Medium"
                            playlistFlag=2
                        } else {
                            //Log.i("myTag","Getting bright prefs")
                            recommendedTracks = getSongRecommendations(accessToken, brightP)
                            mtvLightVal.text = "Light Value: "+currentLightLevel.toString()+" Playlist: Bright"
                            playlistFlag=3
                        }

                        currentIndex = 0
                        if (recommendedTracks.isNotEmpty()) {
                            displaySong(currentIndex)
                            delay(1000)
                        }
                    }
                } else {
                    lifecycleScope.launch {
                        when (playlistFlag) {
                            1 -> {
                                val success = addSongToPlaylist(accessToken, darkID, recommendedTracks[currentIndex].uri)
                                //Log.i("playlistAdd", "dark "+success.toString())
                            }
                            2 -> {
                                val success = addSongToPlaylist(accessToken, mediumID, recommendedTracks[currentIndex].uri)
                                //Log.i("playlistAdd", "medium "+success.toString())
                            }
                            3 -> {
                                val success = addSongToPlaylist(accessToken, brightID, recommendedTracks[currentIndex].uri)
                                //Log.i("playlistAdd", "bright "+success.toString())
                            }
                        }
                    }
                    currentIndex++
                    if(!recommendedTracks.isEmpty()) {
                        displaySong(currentIndex)
                    }
                }

            }
        }


    }

    private fun displaySong(index: Int) {
        if (index == recommendedTracks.size) {
            recommendedTracks = emptyList()
            return
        }
        val song = recommendedTracks[index]
        Picasso.get().load(song.albumImageUrl).into(mivAlbumPic) // load album image using Picasso library
        mtvSongName.text = song.name
        mtvArtistName.text = song.artist
    }
    suspend fun addSongToPlaylist(accessToken: String?, playlistId: String?, songURI: String?): Boolean = withContext(Dispatchers.IO) {
        val addSongUrl = "https://api.spotify.com/v1/playlists/$playlistId/tracks?uris=$songURI"
        val url = URL(addSongUrl)
        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Authorization", "Bearer $accessToken")

        val responseCode = connection.responseCode
        if (responseCode == HttpsURLConnection.HTTP_CREATED) {
            return@withContext true
        } else {
            //Log.e("AddSongToPlaylist", "HTTP error code: $responseCode")
            return@withContext false
        }
    }

    suspend fun getPreviewUrl(trackId: String, accessToken: String?): String? = withContext(Dispatchers.IO) {
        val url = URL("https://api.spotify.com/v1/tracks/$trackId")
        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Authorization", "Bearer $accessToken")

        val responseCode = connection.responseCode
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonResponse = JSONObject(response)
            return@withContext jsonResponse.getString("preview_url")
        } else {
            //.e("GetPreviewUrl", "HTTP error code: $responseCode")
            return@withContext null
        }
    }

    suspend fun getSongRecommendations(accessToken: String?, genres: String?): List<Song> = withContext(Dispatchers.IO) {
        val recommendationUrl = "https://api.spotify.com/v1/recommendations?seed_genres=$genres"
        val url = URL(recommendationUrl)
        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Authorization", "Bearer $accessToken")

        val responseCode = connection.responseCode
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonResponse = JSONObject(response)
            val tracks = jsonResponse.getJSONArray("tracks")
            val songList = mutableListOf<Song>()

            for (i in 0 until tracks.length()) {
                val track = tracks.getJSONObject(i)
                val songName = track.getString("name")
                val songURI = track.getString("uri")
                val artistName = track.getJSONArray("artists").getJSONObject(0).getString("name")
                val albumImageUrl = track.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url")
                val previewUrl=track.getString("preview_url")
                val songID=track.getString("id")
                val song = Song(songName, songURI, artistName, albumImageUrl, previewUrl,songID)
                songList.add(song)
            }

            return@withContext songList
        } else {
            //Log.e("GetSongRecommendations", "HTTP error code: $responseCode")
            return@withContext emptyList()
        }
    }

    private fun measureAmbientLight(): Float {
        return currentLightLevel
    }

    override fun onResume() {
        super.onResume()
        lightSensor?.let {
            sensorManager.registerListener(lightSensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(lightSensorListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }

    private val lightSensorListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                if (it.sensor.type == Sensor.TYPE_LIGHT) {
                    currentLightLevel = event.values[0]
                }
            }
        }
    }




}