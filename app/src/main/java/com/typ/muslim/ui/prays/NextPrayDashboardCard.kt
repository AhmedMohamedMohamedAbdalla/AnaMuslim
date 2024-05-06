/*
 * This app is developed by AHMED SLEEM
 *
 * Copyright (c) 2021.  TYP INC. All Rights Reserved
 */
package com.typ.muslim.ui.prays

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.ImageView
import cn.iwgang.countdownview.CountdownView
import com.mpt.android.stv.Slice
import com.mpt.android.stv.SpannableTextView
import com.typ.muslim.R
import com.typ.muslim.features.prays.PrayerManager
import com.typ.muslim.features.prays.enums.PrayNotifyMethod
import com.typ.muslim.features.prays.enums.PrayType
import com.typ.muslim.features.prays.interfaces.PrayTimeCameListener
import com.typ.muslim.features.prays.models.Pray
import com.typ.muslim.features.ramadan.RamadanManager
import com.typ.muslim.managers.AMSettings
import com.typ.muslim.managers.LocaleManager
import com.typ.muslim.models.Timestamp
import com.typ.muslim.ui.home.DashboardCard
import com.typ.muslim.utils.colorRes
import com.typ.muslim.utils.sp2px
import com.typ.muslim.utils.stringRes
import java.util.Calendar
import java.util.Locale

class NextPrayDashboardCard : DashboardCard {

    // Runtime
    private var locale: Locale
    private var nextPray: Pray
    private var currentPray: Pray
    private var notifyMethod: PrayNotifyMethod

    // Views
    private lateinit var stvNextPrayName: SpannableTextView
    private lateinit var ifvPrayNotifMethod: ImageView
    private lateinit var cdTimeRemaining: CountdownView

    // Listeners
    private lateinit var ptcListener: PrayTimeCameListener

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        // Current locale
        locale = LocaleManager.getCurrLocale(context)
        // Current pray
        currentPray = if (isInEditMode) {
            // [FOR DEVELOPMENT PREVIEW ONLY]
            Pray(PrayType.ISHA, "Isha", Timestamp.NOW())
        } else PrayerManager.getCurrentPray(context)
        // Next pray
        nextPray = if (isInEditMode) {
            // [FOR DEVELOPMENT PREVIEW ONLY]
            Pray(PrayType.MAGHRIB, "MAGHRIB", Timestamp.TOMORROW().set(Calendar.HOUR_OF_DAY, 4))
        } else PrayerManager.getNextPray(context)
        // Notify method for next pray
        notifyMethod = if (isInEditMode) {
            // [FOR DEVELOPMENT PREVIEW ONLY]
            PrayNotifyMethod.values().random()
        } else AMSettings.getPraysNotifyMethod(context)[nextPray.type.ordinal]

        // [DEV LOCALE ONLY]
        if (isInEditMode) locale = LocaleManager.Locales.ARABIC
        refreshUI()
    }

    override fun prepareCardView(context: Context) {
        // Init card
        strokeWidth = 0
        inflate(getContext(), R.layout.layout_next_pray_card, this)
        // Setup content views
        ifvPrayNotifMethod = findViewById(R.id.prayNotifMethodIFV)
        stvNextPrayName = findViewById(R.id.tv_next_pray_name)
        cdTimeRemaining = findViewById(R.id.cdv_next_pray_remaining)
        // Callbacks
        cdTimeRemaining.setOnCountdownEndListener {
            setNextPray(ptcListener.onPrayTimeCame(nextPray))
            refreshUI()
        }
    }

    override fun refreshRuntime() {
        locale = LocaleManager.getCurrLocale(context)
        notifyMethod = AMSettings.getPraysNotifyMethod(context)[nextPray.type.ordinal]
    }

    override fun refreshUI() {
        updateNotifyMethod()
        showNextPrayNameOnTv()
        cdTimeRemaining.start(nextPray.time.toMillis() - System.currentTimeMillis())
    }

    private fun updateNotifyMethod() {
        ifvPrayNotifMethod.setImageResource(
            when (notifyMethod) {
                PrayNotifyMethod.AZAN -> R.drawable.ic_alert_full
                PrayNotifyMethod.NOTIFICATION_ONLY -> R.drawable.ic_alert_notif
                PrayNotifyMethod.OFF -> R.drawable.ic_alert_off
            }
        )
    }

    private fun showNextPrayNameOnTv() {
        stvNextPrayName.apply {
            reset()
            // Pray name
            addSlice(
                Slice.Builder(stringRes(context, nextPray.prayNameRes)).textColor(colorRes(context, R.color.color_primary_90))
                    .textSize(sp2px(context, 20f))
                    .style(Typeface.BOLD)
                    .build()
            )
            // Pray time
            addSlice(
                Slice.Builder(String.format(locale, "\n%s", nextPray.getFormattedTime(context, locale)))
                    .textSize(sp2px(context, 17f))
                    .textColor(colorRes(context, R.color.color_primary_80))
                    .build()
            )
            // Suhur, Iftar, Qiyam slice if in Ramadan
            if (RamadanManager.isInRamadan() && nextPray.type in arrayOf(PrayType.FAJR, PrayType.MAGHRIB, PrayType.ISHA)) {
                val sliceText = when (nextPray.type) {
                    PrayType.FAJR -> stringRes(context, R.string.fasting)
                    PrayType.MAGHRIB -> stringRes(context, R.string.iftar)
                    PrayType.ISHA -> stringRes(context, R.string.qiyam)
                    else -> ""
                }
                if (sliceText.isNotBlank()) {
                    addSlice(
                        Slice.Builder(String.format(locale, " (%s)", sliceText))
                            .textSize(sp2px(11f))
                            .textColor(colorRes(context, R.color.color_neutral_80))
                            .build()
                    )
                }
            }
            // Tomorrow if before 12 am next day and next pray is FAJR
            if (nextPray.type == PrayType.FAJR && nextPray.time.isAfter(Timestamp.NOW())) {
                stvNextPrayName.addSlice(
                    Slice.Builder(String.format(locale, " (%s)", stringRes(context, R.string.tomorrow)))
                        .textSize(sp2px(11f))
                        .textColor(colorRes(context, R.color.color_neutral_80))
                        .build()
                )
            }
            stvNextPrayName.display()
        }
    }

    fun setNextPray(nextPray: Pray) {
        this.currentPray = this.nextPray
        this.nextPray = if (!nextPray.passed) nextPray else PrayerManager.getNextPray(context)
    }

    fun setPrayTimeCameListener(listener: PrayTimeCameListener) {
        ptcListener = listener
    }

    override fun toString() = "NextPrayDashboardCard"

    override  fun onTimeChanged(now: Timestamp) {
         setNextPray(PrayerManager.getNextPray(context))
         refreshUI()
     }

}