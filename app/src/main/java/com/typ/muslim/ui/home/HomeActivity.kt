/*
 * This app is developed by AHMED SLEEM
 *
 * Copyright (c) 2021.  TYP INC. All Rights Reserved
 */
package com.typ.muslim.ui.home

import android.content.Intent
import android.view.KeyEvent
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import com.typ.muslim.R
import com.typ.muslim.app.Keys
import com.typ.muslim.features.prays.PrayerManager.getNextPray
import com.typ.muslim.features.prays.PrayerManager.getTodayPrays
import com.typ.muslim.features.prays.interfaces.PrayTimeCameListener
import com.typ.muslim.features.prays.models.Pray
import com.typ.muslim.features.prays.models.PrayTimes
import com.typ.muslim.interfaces.TimeChangedListener
import com.typ.muslim.models.Timestamp
import com.typ.muslim.ui.calendar.HijriCalendarActivity
import com.typ.muslim.ui.core.AnaMuslimActivity
import com.typ.muslim.ui.khatma.dashboard.KhatmaDashboardCard
import com.typ.muslim.ui.names.AllahNamesDashboardCard
import com.typ.muslim.ui.names.HolyNameOfAllahDescActivity
import com.typ.muslim.ui.prays.PraysActivity
import com.typ.muslim.ui.tracker.dashboard.TrackerDashboardCard

class HomeActivity : AnaMuslimActivity(R.layout.activity_home), PrayTimeCameListener, TimeChangedListener {

    // Runtime
    private var madeFirstSetup = false
    private lateinit var nextPray: Pray
    private lateinit var prayTimes: PrayTimes
    private lateinit var homeCards: HomeCards

    override fun findViews() {
        homeCards = HomeCards(
            nextPray = findViewById(R.id.next_pray_card),
            khatma = findViewById(R.id.khatma_card),
            tasbeeh = findViewById(R.id.tasbeeh_card),
            hijriCalendar = findViewById(R.id.hijri_calendar_card),
            prayTracker = findViewById(R.id.pray_tracker_card),
            namesOfAllah = findViewById(R.id.names_of_allah_card)
        )
    }

    override fun initRuntime() {
        prayTimes = getTodayPrays(this)
        nextPray = getNextPray(this, prayTimes)
    }

    override fun initUI() {
        homeCards.nextPray.setNextPray(nextPray)
    }

    override fun initListeners() {
        homeCards.nextPray.apply {
            setPrayTimeCameListener(this@HomeActivity)
            setOnClickListener { v: View ->
                val op = ActivityOptionsCompat.makeSceneTransitionAnimation(this@HomeActivity, v, "transition_card_to_activity")
                startActivity(Intent(this@HomeActivity, PraysActivity::class.java), op.toBundle())
            }
        }
        homeCards.prayTracker.setOnClickListener { v: View -> (v as TrackerDashboardCard).onClick(this) }
        homeCards.hijriCalendar.setOnClickListener { v: View ->
            val op = ActivityOptionsCompat.makeSceneTransitionAnimation(this, v, "transition_card_to_activity")
            startActivity(Intent(this, HijriCalendarActivity::class.java), op.toBundle())
        }
        homeCards.namesOfAllah.setOnClickListener { v: View ->
            val op = ActivityOptionsCompat.makeSceneTransitionAnimation(this, v, "transition_card_to_activity")
            val intent = Intent(this, HolyNameOfAllahDescActivity::class.java)
            intent.putExtra(Keys.NAME_OF_ALLAH, (v as AllahNamesDashboardCard).holdingName)
            startActivity(intent, op.toBundle())
        }
        homeCards.khatma.setOnClickListener { v: View -> (v as KhatmaDashboardCard).handleClick(this) }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val isHandled = homeCards.tasbeeh.handleKeyEvent(keyCode)
        return isHandled || super.onKeyDown(keyCode, event)
    }

    override fun onPrayTimeCame(pray: Pray): Pray {
        homeCards.listed.forEach {
            if (it is PrayTimeCameListener) it.onPrayTimeCame(pray)
        }
        return getNextPray(this)
    }

    override fun onTimeChanged(now: Timestamp) {
        // Notify all cards to refresh its runtime and its UI
        homeCards.listed.forEach { card: DashboardCard -> card.onTimeChanged(now) }
    }

    override fun onResume() {
        super.onResume()
        if (madeFirstSetup) homeCards.listed.forEach { obj: DashboardCard -> obj.performRefresh() }
        madeFirstSetup = true
    }
}
