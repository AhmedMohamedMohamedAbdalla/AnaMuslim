/*
 * This app is developed by AHMED SLEEM
 *
 * Copyright (c) 2021.  TYP INC. All Rights Reserved
 */
package com.typ.muslim.ui.setup

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.typ.muslim.R
import com.typ.muslim.profile.ProfileManager
import com.typ.muslim.ui.home.HomeActivity

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install SplashScreen
//        var shouldWait = true
//        val timer = object : CountDownTimer(2 * 1000, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//            }
//
//            override fun onFinish() {
//                shouldWait = false
//            }
//
//        }
//        timer.start()
//        val splashScreen = installSplashScreen().apply {
//            setKeepOnScreenCondition { shouldWait }
//        }
        // Init LauncherActivity
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        supportActionBar?.hide() // Hide default ActionBar.
        // Go to dashboard if user has profile
        val timer = object : CountDownTimer(2 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                if (ProfileManager.hasProfile(this@LauncherActivity)) {
                    startActivity(Intent(this@LauncherActivity, HomeActivity::class.java))
                    finishAfterTransition()
                }
            }
        }
        timer.start()

    }
}