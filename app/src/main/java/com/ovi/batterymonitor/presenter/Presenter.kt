package com.ovi.batterymonitor.presenter

import com.ovi.batterymonitor.view.View

interface Presenter<in T : View> {
    fun onCreate(view: T)

    fun onDestroy()
}