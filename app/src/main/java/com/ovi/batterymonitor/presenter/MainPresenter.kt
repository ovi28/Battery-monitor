package com.ovi.batterymonitor.presenter

import android.content.Context
import android.content.Context.BATTERY_SERVICE
import com.ovi.batterymonitor.model.Status
import com.ovi.batterymonitor.model.Vitals
import com.ovi.batterymonitor.view.MainActivity
import android.os.BatteryManager
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler

class MainPresenter : Presenter<MainActivity> {

    private var mainActivity: MainActivity? = null
    private var status: Status? = null
    private var vitals: Vitals? = null
    override fun onCreate(view: MainActivity) {
        mainActivity = view
        repetitiveUpdate()
    }

    private fun repetitiveUpdate(){
        val handler = Handler()
        val runnableCode = object : Runnable {
            override fun run() {
                calculateStatus()
                calculateVitals()
                handler.postDelayed(this, 500);
            }
        }
        handler.post(runnableCode)

    }

    override fun onDestroy() {
        mainActivity = null
    }

    private fun calculateStatus() {
        val mBatteryManager = mainActivity!!.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val avgCurrent: Long?
        avgCurrent = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)
        val batStatus = getChargingStatus()
        status = Status(batStatus, "$avgCurrent mAh")
        mainActivity!!.showStatus(status!!)
    }


    private fun getChargingStatus(): String {
        var chargingMethod = "";
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            mainActivity!!.registerReceiver(null, ifilter)
        }
        val batStatus: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging: Boolean = batStatus == BatteryManager.BATTERY_STATUS_CHARGING
                || batStatus == BatteryManager.BATTERY_STATUS_FULL

        val chargePlug: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        val usbCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
        val acCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_AC
        if (isCharging) {
            if (usbCharge) {
                chargingMethod = "Charging via USB"
            } else if (acCharge) {
                chargingMethod = "Charging via AC"
            }
        } else {
            chargingMethod = "Discharging"
        }
        return chargingMethod
    }

    private fun calculateVitals() {
        val bm = mainActivity!!.getSystemService(BATTERY_SERVICE) as BatteryManager
        val batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val intent = mainActivity!!.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val batTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10
        val status = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)
        val batHealth = getHealth(status)
        vitals = Vitals("$batLevel%", "$batTemp °C", batHealth)
        mainActivity!!.showVitals(vitals!!)
    }

    private fun getHealth(status: Int): String {
        if (status == BatteryManager.BATTERY_HEALTH_COLD) {
            return "Cold"
        }
        if (status == BatteryManager.BATTERY_HEALTH_DEAD) {
            return "Dead"
        }
        if (status == BatteryManager.BATTERY_HEALTH_GOOD) {
            return "Good"
        }
        if (status == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
            return "Over heat"
        }
        if (status == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE) {
            return "Over voltage"
        }
        if (status == BatteryManager.BATTERY_HEALTH_UNKNOWN) {
            return "Unknown"
        }
        if (status == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
            return "Unspecified failure"
        }
        return "Unknown"
    }


}