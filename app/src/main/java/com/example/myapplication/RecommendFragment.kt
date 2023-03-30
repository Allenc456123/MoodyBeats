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
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue
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

    private lateinit var recommendedTracks: List<Song>

    private lateinit var sensorManager: SensorManager
    private lateinit var lightSensor: Sensor
    var genres ="pop,classical,jazz"
    data class Song(
        val name: String,
        val id: String,
        val artist: String,
        val albumImageUrl: String
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
        var currentIndex = 0
        // Here you can access your views and add your logic
        mBtnCheckLight = view.findViewById<Button>(R.id.getSong)
        mtvSongName = view.findViewById(R.id.songName)
        mtvArtistName = view.findViewById(R.id.artist)
        mivAlbumPic = view.findViewById(R.id.albumPic)
        mtvLightVal = view.findViewById<TextView>(R.id.lightVal)

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        recommendedTracks = emptyList()
        mBtnCheckLight.setOnClickListener {
            var measure = measureAmbientLight()
            mtvLightVal.text = measure.toString()
            if (recommendedTracks.isEmpty() || currentIndex >= recommendedTracks.size) {
                lifecycleScope.launch {
                    recommendedTracks = getSongRecommendations(accessToken, genres)
                    currentIndex = 0
                    if (recommendedTracks.isNotEmpty()) {
                        displaySong(currentIndex)
                        delay(1000)
                    }
                }
            } else {
                currentIndex++
                displaySong(currentIndex)
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


//    fun measureAmbientLight(context: Context): Float {
//        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
//        var lightValue = -1.0f
//
//        val sensorListener = object : SensorEventListener {
//            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
//
//            override fun onSensorChanged(event: SensorEvent?) {
//                if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
//                    lightValue = event.values[0]
//                    sensorManager.unregisterListener(this)
//                }
//            }
//        }
//
//        sensorManager.registerListener(sensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
//
//        // Wait for the sensor to return a value
//        while (lightValue < 0.0f) {
//            Thread.sleep(50)
//        }
//
//        return lightValue
//    }

    private fun measureAmbientLight() {
        sensorManager.registerListener(sensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private val sensorListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
                val lightValue = event.values[0]
                mtvLightVal.text = lightValue.toString()
            }
        }
    }
    suspend fun getSongRecommendations(accessToken: String?, genres: String): List<Song> = withContext(Dispatchers.IO) {
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
                val songId = track.getString("id")
                val artistName = track.getJSONArray("artists").getJSONObject(0).getString("name")
                val albumImageUrl = track.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url")
                val song = Song(songName, songId, artistName, albumImageUrl)
                songList.add(song)
            }

            return@withContext songList
        } else {
            Log.e("GetSongRecommendations", "HTTP error code: $responseCode")
            return@withContext emptyList()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        sensorManager.unregisterListener(sensorListener)
    }






}