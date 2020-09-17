package org.sadoke.worldboard.sensormanager

interface OnSensorDataChangeListener {
    /**
     * To be called from Tracker Classes when Sensor is changed to notify MainActivity
     */
    fun onSensorDataChanged()

    /**
     * Add Logger records
     * @param str Text to be logged
     */
    fun setLogRecord(str: String)
}