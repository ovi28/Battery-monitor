package com.ovi.batterymonitor.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ovi.batterymonitor.R
import com.ovi.batterymonitor.model.Status
import com.ovi.batterymonitor.model.Vitals
import com.ovi.batterymonitor.presenter.MainPresenter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.status_battery_info_layout.*
import kotlinx.android.synthetic.main.vitals_battery_info_layout.*


class MainActivity : AppCompatActivity(),View {

    lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        initPresenter()
    }

    private fun initPresenter() {
        presenter = MainPresenter()
        presenter.onCreate(this)
    }

    fun showStatus(status: Status){
        status_value_text.text = status.status
        current_value_text.text = status.current
    }

    fun showVitals(vitals: Vitals){
        percentage_value_text.text = vitals.percentage
        temperature_value_text.text = vitals.temperature
        health_value_text.text = vitals.health
    }


}
