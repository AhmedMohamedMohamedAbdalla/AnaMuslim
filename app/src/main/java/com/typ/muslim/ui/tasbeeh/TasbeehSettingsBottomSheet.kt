package com.typ.muslim.ui.tasbeeh

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.typ.muslim.R
import com.typ.muslim.features.tasbeeh.enums.TasbeehMode
import com.typ.muslim.features.tasbeeh.enums.TasbeehTimes
import com.typ.muslim.features.tasbeeh.models.TasbeehConfig
import com.typ.muslim.interfaces.ResultCallback
import com.typ.muslim.ui.views.SwitcherCard

class TasbeehSettingsBottomSheet(
    val ctx: Context,
    val config: TasbeehConfig,
    val callback: ResultCallback<TasbeehConfig>
) {

    // UI
    private val bs: BottomSheetDialog = BottomSheetDialog(ctx)
    private val btgMode: MaterialButtonToggleGroup
    private val btgCounter: MaterialButtonToggleGroup
    private val swcUseVolumeCounter: SwitcherCard
    private val swcVibrateOnCount: SwitcherCard
    private val swcSpeakTasbeeh: SwitcherCard

    init {
        bs.apply {
            // Init BottomSheet
            setContentView(R.layout.bs_tasbeeh_options)
            behavior.isFitToContents = true
            // Init views
            btgMode = findViewById<MaterialButtonToggleGroup>(R.id.btg_mode)?.apply {
                check(
                    when (config.mode) {
                        TasbeehMode.FREE_MODE -> R.id.btn_free_mode
                        else -> R.id.btn_tasbeeh_mode
                    }
                )
            }!!
            btgCounter = findViewById<MaterialButtonToggleGroup>(R.id.btg_counter)?.apply {
                check(
                    when (config.times) {
                        33 -> R.id.btn_33_times
                        66 -> R.id.btn_66_times
                        99 -> R.id.btn_99_times
                        else -> R.id.btn_inf_times
                    }
                )
            }!!

            swcUseVolumeCounter = findViewById<SwitcherCard>(R.id.swc_volume_counter)?.apply {
                check(config.isVolumeCounterEnabled)
            }!!
            swcVibrateOnCount = findViewById<SwitcherCard>(R.id.swc_vibrate_on_tick)?.apply {
                check(config.isVibrationEnabled)
            }!!
            swcSpeakTasbeeh = findViewById<SwitcherCard>(R.id.swc_speak_tasbeeh)?.apply {
                check(config.isSpeakTasbeehOnFlipEnabled)
            }!!

            findViewById<MaterialButton>(R.id.btn_reset_tasbeeh_settings)?.setOnClickListener {
                btgMode.check(R.id.btn_tasbeeh_mode)
                btgCounter.check(R.id.btn_66_times)
                swcUseVolumeCounter.check(true)
                swcVibrateOnCount.check(true)
                swcSpeakTasbeeh.check(true)
            }
            findViewById<MaterialButton>(R.id.btn_apply_tasbeeh_settings)?.setOnClickListener {
                callback.onResult(
                    TasbeehConfig(
                        times = when (btgCounter.checkedButtonId) {
                            R.id.btn_33_times -> TasbeehTimes.TIMES_33.howMany
                            R.id.btn_66_times -> TasbeehTimes.TIMES_66.howMany
                            R.id.btn_99_times -> TasbeehTimes.TIMES_99.howMany
                            else -> TasbeehTimes.TIMES_INFINITE.howMany
                        },
                        mode = when (btgMode.checkedButtonId) {
                            R.id.btn_free_mode -> TasbeehMode.FREE_MODE
                            else -> TasbeehMode.TASBEEH_MODE
                        },
                        isVibrationEnabled = swcVibrateOnCount.isOn,
                        isVolumeCounterEnabled = swcUseVolumeCounter.isOn,
                        isSpeakTasbeehOnFlipEnabled = swcSpeakTasbeeh.isOn
                    )
                )
                cancel()
            }
        }
    }

    fun show() = bs.show()

}