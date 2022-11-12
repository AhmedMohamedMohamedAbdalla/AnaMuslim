/*
 * This app is developed by AHMED SLEEM
 *
 * Copyright (c) 2021.  TYP INC. All Rights Reserved
 */

package com.typ.muslim.ui;

import static com.typ.muslim.core.praytime.enums.Prays.FAJR;
import static com.typ.muslim.core.praytime.enums.Prays.ISHA;
import static com.typ.muslim.core.praytime.enums.Prays.MAGHRIB;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.material.textview.MaterialTextView;
import com.mpt.android.stv.Slice;
import com.mpt.android.stv.SpannableTextView;
import com.typ.muslim.R;
import com.typ.muslim.app.Keys;
import com.typ.muslim.core.praytime.enums.Prays;
import com.typ.muslim.enums.PrayNotifyMethod;
import com.typ.muslim.managers.AMSettings;
import com.typ.muslim.managers.PrayerManager;
import com.typ.muslim.managers.ResMan;
import com.typ.muslim.models.Pray;
import com.typ.muslim.ramadan.RamadanManager;
import com.typ.muslim.ui.dashboard.DashboardCard;
import com.typ.muslim.ui.dashboard.prays.VerticalPraysDashboardCard;

import java.util.Locale;

public class VerticalPrayView extends DashboardCard {

    // Statics
    private static final String TAG = "PrayItemView";
    // Runtime
    private Pray pray;
    private PrayNotifyMethod notifyMethod;
    // Views
    private RelativeLayout rlContainer;
    private ImageView ivIndicator;
    private SpannableTextView tvPrayName;
    private MaterialTextView tvPrayTime;
    private ImageButton ibtnChangeNotifyMethod;
    // Callbacks
    private VerticalPraysDashboardCard.PrayNotifyMethodChangedCallback callback;

    public VerticalPrayView(Context context) {
        super(context);
    }

    public VerticalPrayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalPrayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void parseAttrs(Context context, AttributeSet attrs) {
        // Used only to view different data on each PrayItemView during design
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VerticalPrayView);
        Prays pray = Prays.valueOf(typedArray.getInt(R.styleable.VerticalPrayView_pivPray, FAJR.ordinal()));
        this.pray = new Pray(pray, pray.name(), System.currentTimeMillis());
        this.notifyMethod = AMSettings.getPrayNotifyMethod(context, this.pray.getType());
        typedArray.recycle();
    }

    @Override
    public void prepareCardView(Context context) {
        // Inflate card view and prepare card
        inflate(context, R.layout.layout_pray_item, this);
        setCardBackgroundColor(getColor(R.color.adaptiveBackgroundColor));
        setRippleColorResource(R.color.transparent);
        // Setup inner views
        rlContainer = findViewById(R.id.rl_piv_container);
        ivIndicator = findViewById(R.id.prayIndicatorIV);
        tvPrayName = findViewById(R.id.prayNameTV);
        tvPrayTime = findViewById(R.id.prayTimeTV);
        ibtnChangeNotifyMethod = findViewById(R.id.changePrayNotifyMethodIB);
        // Listeners and Callbacks
        ibtnChangeNotifyMethod.setOnClickListener(this);
        // Refresh UI
        this.setPray(pray);
    }

    public VerticalPrayView setPray(Pray pray) {
        // Null check
        if (pray == null) return this;
        // Update runtime
        this.pray = pray;
        final Pray nextPray = isInEditMode() ? pray : PrayerManager.getNextPray(getContext());
        // PrayName
        this.tvPrayName.reset();
        this.tvPrayName.addSlice(new Slice.Builder(getString(pray.getPrayNameRes()))
                .style(Typeface.BOLD)
                .textSize(sp2px(16f))
                .textColor(getColor(isPrayPassed() ? R.color.green : nextPray.equals(pray) ? pray.getSurfaceColorRes() : R.color.darkAdaptiveColor))
                .build());
        if (RamadanManager.isInRamadan() && (pray.getType() == FAJR || pray.getType() == MAGHRIB || pray.getType() == ISHA)) {
            final String sliceText;
            if (pray.getType() == FAJR) sliceText = getString(R.string.suhur);
            else if (pray.getType() == MAGHRIB) sliceText = getString(R.string.iftar);
            else if (pray.getType() == ISHA) sliceText = getString(R.string.qiyam);
            else sliceText = "";
            this.tvPrayName.addSlice(new Slice.Builder(String.format(Locale.getDefault(), "  (%s)", sliceText))
                    .textSize(sp2px(10f))
                    .textColor(getColor(isPrayPassed() ? R.color.green : nextPray.equals(pray) ? pray.getType().getSurfaceColorRes() : R.color.darkAdaptiveColor))
                    .build());
        }
        this.tvPrayName.display();
        // PrayTime and indicators
        this.tvPrayTime.setTextColor(getColor(isPrayPassed() ? R.color.green : nextPray.equals(pray) ? pray.getType().getSurfaceColorRes() : R.color.darkAdaptiveColor));
        this.tvPrayTime.setText(this.pray.getFormattedTime(getContext()));
        this.changeIndicator(nextPray);
        this.updateNotifyMethodView();
        return this;
    }

    public void changeIndicator(Pray nextPray) {
        if (this.isPrayPassed()) {
            // Passed
            ivIndicator.setImageResource(R.drawable.ic_done);
            ivIndicator.setColorFilter(getColor(R.color.white));
            ivIndicator.setBackgroundResource(R.drawable.shape_passed_pray);
            this.setCardBackgroundColor(getColor(R.color.adaptiveBackgroundColor));
            ivIndicator.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.green)));
        } else if (pray.equals(nextPray)) {
            // Next
            final int surfaceColor = getColor(pray.getSurfaceColorRes());
            final int onSurfaceColor = getColor(pray.getOnSurfaceColorRes());
            this.setCardBackgroundColor(getColor(R.color.adaptiveBackgroundColor));
            ivIndicator.setColorFilter(surfaceColor);
            ivIndicator.setImageResource(R.drawable.ic_arrow_to_right);
            ivIndicator.setBackgroundResource(R.drawable.shape_next_pray);
            ivIndicator.setBackgroundTintList(ColorStateList.valueOf(surfaceColor));
        } else {
            // Coming
            ivIndicator.setImageDrawable(null);
            ivIndicator.setBackgroundResource(R.drawable.shape_coming_pray);
            tvPrayName.setTextColor(getColor(R.color.darkAdaptiveColor));
            tvPrayTime.setTextColor(getColor(R.color.darkAdaptiveColor));
            ivIndicator.setColorFilter(getColor(R.color.darkAdaptiveColor));
            ivIndicator.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.darkAdaptiveColor)));
            this.setCardBackgroundColor(getColor(R.color.adaptiveBackgroundColor));
        }
    }

    public void updateNotifyMethodView() {
        if (notifyMethod == null) return;
        if (this.pray.getType() == Prays.SUNRISE) {
            this.ibtnChangeNotifyMethod.setEnabled(false);
            this.ibtnChangeNotifyMethod.setImageResource(R.drawable.ic_notify_off);
            this.ibtnChangeNotifyMethod.setColorFilter(ResMan.getColor(getContext(), R.color.red));
            this.ibtnChangeNotifyMethod.setBackgroundTintList(ColorStateList.valueOf(ResMan.getColor(getContext(), R.color.bg_input_box)));
            return;
        }
        // Update view
        if (this.notifyMethod == PrayNotifyMethod.AZAN) {
            this.ibtnChangeNotifyMethod.setImageResource(R.drawable.ic_notify_with_sound);
            this.ibtnChangeNotifyMethod.setBackgroundTintList(ColorStateList.valueOf(ResMan.getColor(getContext(), R.color.green)));
            this.ibtnChangeNotifyMethod.setColorFilter(ResMan.getColor(getContext(), R.color.white));
        } else if (this.notifyMethod == PrayNotifyMethod.NOTIFICATION_ONLY) {
            this.ibtnChangeNotifyMethod.setImageResource(R.drawable.ic_notify_without_sound);
            this.ibtnChangeNotifyMethod.setBackgroundTintList(ColorStateList.valueOf(ResMan.getColor(getContext(), R.color.colorPrimary)));
            this.ibtnChangeNotifyMethod.setColorFilter(ResMan.getColor(getContext(), R.color.white));
        } else {
            this.ibtnChangeNotifyMethod.setImageResource(R.drawable.ic_notify_off);
            this.ibtnChangeNotifyMethod.setBackgroundTintList(ColorStateList.valueOf(ResMan.getColor(getContext(), R.color.red)));
            this.ibtnChangeNotifyMethod.setColorFilter(ResMan.getColor(getContext(), R.color.white));
        }
    }

    public boolean isPrayPassed() {
        if (pray == null) return true;
        return pray.hasPassed();
    }

    public void setCallback(VerticalPraysDashboardCard.PrayNotifyMethodChangedCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.changePrayNotifyMethodIB) {
            if (pray == null) return;
            if (this.notifyMethod == PrayNotifyMethod.AZAN) this.notifyMethod = PrayNotifyMethod.NOTIFICATION_ONLY;
            else if (this.notifyMethod == PrayNotifyMethod.NOTIFICATION_ONLY) this.notifyMethod = PrayNotifyMethod.OFF;
            else if (this.notifyMethod == PrayNotifyMethod.OFF) {
                if (pray.getType() == Prays.SUNRISE) this.notifyMethod = PrayNotifyMethod.NOTIFICATION_ONLY;
                else this.notifyMethod = PrayNotifyMethod.AZAN;
            }
            // Save new notify method in settings
            AMSettings.save(getContext(), Keys.PRAY_NOTIFY_METHOD(pray.getType()), this.notifyMethod);
            // Update views
            this.updateNotifyMethodView();
            // Notify callback
            if (callback != null) callback.onPrayNotifyMethodChanged(pray.getType(), this.notifyMethod);
        }
    }

}
