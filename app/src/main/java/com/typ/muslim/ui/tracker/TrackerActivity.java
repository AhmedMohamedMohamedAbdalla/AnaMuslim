/*
 * This app is developed by AHMED SLEEM
 *
 * Copyright (c) 2022.  TYP INC. All Rights Reserved
 */

package com.typ.muslim.ui.tracker;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.typ.muslim.R;

public class TrackerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);
        // Hide default actionBar
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        // Init runtime
        ((MaterialToolbar) findViewById(R.id.toolbar)).setNavigationOnClickListener(v-> {
            finishAfterTransition();
        });
        // Init views
    }
}
