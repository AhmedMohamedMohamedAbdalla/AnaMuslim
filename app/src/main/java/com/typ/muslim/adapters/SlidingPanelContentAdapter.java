/*
 * This app is developed by AHMED SLEEM
 *
 * Copyright (c) 2021.  TYP INC. All Rights Reserved
 */

package com.typ.muslim.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.typ.muslim.features.prays.PrayNotifyMethodChangedCallback;
import com.typ.muslim.features.prays.models.PrayTimes;
import com.typ.muslim.ui.fragments.MenuFragment;
import com.typ.muslim.ui.fragments.QiblaFragment;

import org.jetbrains.annotations.NotNull;

public class SlidingPanelContentAdapter extends FragmentStatePagerAdapter {

    private final PrayTimes prays;
    private final PrayNotifyMethodChangedCallback callback;


    public SlidingPanelContentAdapter(@NonNull @NotNull FragmentManager fm, PrayTimes prays, PrayNotifyMethodChangedCallback callback) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.prays = prays;
        this.callback = callback;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        return position == 0 ? new QiblaFragment() /* Qibla (Left) */ : new MenuFragment() /* Menu (Right) */;
    }

    @Override
    public int getCount() {
        return 3;
    }

}
