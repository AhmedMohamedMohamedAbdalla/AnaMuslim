package com.typ.muslim.ui.prays

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.transition.TransitionManager
import cn.iwgang.countdownview.CountdownView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import com.typ.muslim.R
import com.typ.muslim.enums.FormatPattern
import com.typ.muslim.features.prays.PrayerManager
import com.typ.muslim.features.prays.models.Pray
import com.typ.muslim.features.prays.models.PrayTimes
import com.typ.muslim.interfaces.TimeChangedListener
import com.typ.muslim.managers.AMSettings
import com.typ.muslim.models.Location
import com.typ.muslim.models.Timestamp
import com.typ.muslim.ui.core.AnaMuslimActivity
import com.typ.muslim.utils.stringRes
import java.util.Calendar

class PraysActivity : AnaMuslimActivity(R.layout.activity_prays), TimeChangedListener {

    // Runtime
    private lateinit var nextPray: Pray
    private lateinit var location: Location
    private lateinit var prayTimes: PrayTimes
    private var selectedDate = Timestamp.NOW()

    private var nextPrayChanged = true

    private val isShowingToday: Boolean
        get() = selectedDate.isToday

    // Views
    private lateinit var tvLocation: MaterialTextView
    private lateinit var fabClose: FloatingActionButton
    private lateinit var tvPrayName: MaterialTextView
    private lateinit var tvPrayTime: MaterialTextView
    private lateinit var cdvRemaining: CountdownView
    private lateinit var fabPrevDay: FloatingActionButton
    private lateinit var tvSelectedDate: MaterialTextView
    private lateinit var fabNextDay: FloatingActionButton
    private lateinit var dailyPraysView: DailyPraysView

    override fun findViews() {
        tvLocation = findViewById(R.id.tv_location)
        fabClose = findViewById(R.id.fab_close)
        tvPrayName = findViewById(R.id.tv_pray_name)
        tvPrayTime = findViewById(R.id.tv_pray_time)
        cdvRemaining = findViewById(R.id.cdv_next_pray_remaining)
        fabPrevDay = findViewById(R.id.fab_prev_day)
        tvSelectedDate = findViewById(R.id.tv_selected_day)
        fabNextDay = findViewById(R.id.fab_next_day)
        dailyPraysView = findViewById(R.id.daily_prays_view)
    }

    override fun initRuntime() {
        location = AMSettings.getCurrentLocation(this)
        prayTimes = PrayerManager.getPrays(this, selectedDate)
        nextPray = PrayerManager.getNextPray(this, prayTimes)

        nextPrayChanged= true
    }

    @SuppressLint("SetTextI18n")
    override fun initUI() {
        TransitionManager.beginDelayedTransition(root)

        tvLocation.text = "${location.cityName}, ${location.countryName}"

        if (isShowingToday && nextPrayChanged) {
            tvPrayName.text = stringRes(this, nextPray.prayNameRes)
            tvPrayTime.text = nextPray.getFormattedTime(this)
            cdvRemaining.start(nextPray.time.toMillis() - System.currentTimeMillis())
        }
        // todo: Show HijriDate as primary line and GeorgDate as secondary line
        tvSelectedDate.text = selectedDate.getFormatted(FormatPattern.DATE_FULL)

        dailyPraysView.display(prayTimes, nextPray)
    }

    override fun initListeners() {
        fabClose.setOnClickListener { finishAfterTransition() }

        cdvRemaining.setOnCountdownEndListener {
            initRuntime()
            initUI()
        }

        fabPrevDay.setOnClickListener { rollDate(-1) }
        fabPrevDay.setOnLongClickListener {
            if (!isShowingToday) jumpToToday()
            true
        }

        tvSelectedDate.setOnClickListener {
            if (!isShowingToday) jumpToToday()
        }

        fabNextDay.setOnClickListener { rollDate(1) }
    }

    private fun rollDate(amount: Int) {
        selectedDate.roll(Calendar.DATE, amount)
        initRuntime()
        initUI()
    }

    private fun jumpToDate(timestamp: Timestamp) {
        selectedDate = timestamp
        rollDate(0)
    }

    private fun jumpToToday() {
        jumpToDate(Timestamp.NOW())
    }

    override fun onTimeChanged(now: Timestamp) {
        selectedDate = now

        initRuntime()
        initUI()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // todo: Calculate swipe delta in x-axis then if it passes the threshold, move prev or next according to direction.
        return super.onTouchEvent(event)
    }

}