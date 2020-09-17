package org.sadoke.worldboard

import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.pow
import kotlin.math.sqrt

class SensorManagementClass(private val activity: MainActivity) : SensorEventListener {
    private val locMan: LocationManager = activity.getSystemService(LOCATION_SERVICE) as LocationManager
    private val senMan: SensorManager = activity.getSystemService(SENSOR_SERVICE) as SensorManager
    private val multiplier = 1000
    private var windowCounter = 0
    private var windows = JSONArray()
    private var mLocation: Location? = null
    private val handler = Handler()
    private val runnable = object : Runnable {
        override fun run() {
            senMan.registerListener(
                this@SensorManagementClass,
                senMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                0
            )
            handler.postDelayed(this, 400)
        }
    }

    companion object {
        const val MAX_WINDOWS = 6
    }

    private val locationListener = object : LocationListener{
        override fun onLocationChanged(p0: Location?) {
            // Nothing to do here
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
            // Nothing to do here
        }

        override fun onProviderEnabled(p0: String?) {
            // Nothing to do here
        }

        override fun onProviderDisabled(p0: String?) {
            // Nothing to do here
        }
    }

    @SuppressLint("MissingPermission")
    fun start() {
        locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0.0f, locationListener)
        handler.post(runnable)
    }

    fun stop() {
        handler.removeCallbacks(runnable)
        locMan.removeUpdates(locationListener)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // Nothing to do here
    }

    @SuppressLint("MissingPermission")
    override fun onSensorChanged(p0: SensorEvent) {
        val loc = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (windowCounter == 0)
            mLocation = loc
        if (mLocation != null && loc != null) {
            windows.put(windowCounter, JSONObject().apply {
                put("accelX", p0.values[0].toDouble())
                put("accelY", p0.values[1].toDouble())
                put("accelZ", p0.values[2].toDouble())
            })
            if (windowCounter == MAX_WINDOWS - 1) {
                val lat = (loc.latitude - mLocation!!.latitude) * multiplier
                val lng = (loc.longitude - mLocation!!.longitude) * multiplier

                var a: Int? = null
                activity.sendLogs(JSONObject().apply {
                    put("id", a ?: JSONObject.NULL)
                    put("accel", windows)
                    put("movementVector", sqrt(lat.pow(2.0) + lng.pow(2.0)))
                })
                windows = JSONArray()
            }
            windowCounter++
            if (windowCounter > MAX_WINDOWS - 1) windowCounter = 0
        }

        //activity.setChange(arr.size.toString())

        senMan.unregisterListener(this)
    }
}