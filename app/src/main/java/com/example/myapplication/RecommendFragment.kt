package com.example.myapplication

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue


/**
 * A simple [Fragment] subclass.
 * Use the [RecommendFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecommendFragment : Fragment() {

    private lateinit var mBtnCheckLight: Button
    private lateinit var mtvRecommendFragment: TextView

    private lateinit var sensorManager: SensorManager
    private lateinit var lightSensor: Sensor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recommend, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Here you can access your views and add your logic
        mBtnCheckLight = view.findViewById<Button>(R.id.btnLightCheck)
        mtvRecommendFragment = view.findViewById<TextView>(R.id.tvRecommendFragment)

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        mBtnCheckLight.setOnClickListener {
            var measure = measureAmbientLight()
            mtvRecommendFragment.text = measure.toString()
        }


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
                mtvRecommendFragment.text = lightValue.toString()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sensorManager.unregisterListener(sensorListener)
    }






}