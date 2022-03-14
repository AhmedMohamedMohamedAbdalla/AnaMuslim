/*
 * This app is developed by AHMED SLEEM
 *
 * Copyright (c) 2021.  TYP INC. All Rights Reserved
 */

package com.typ.muslim.ui;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.telecom.TelecomManager;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.typ.muslim.interfaces.ViewHelperMethods;
import com.typ.muslim.managers.ResMan;
import com.typ.muslim.utils.DisplayUtils;

public class BaseView extends View implements ViewHelperMethods {

	public BaseView(Context context) {
		super(context);
		parseAttrs(context, null);
		setupRuntime(context);
	}

	public BaseView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		parseAttrs(context, attrs);
		setupRuntime(context);
	}

	public BaseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		parseAttrs(context, attrs);
		setupRuntime(context);
	}

	public void parseAttrs(@NonNull Context context, @Nullable AttributeSet attrs) {
	}

	public void setupRuntime(@NonNull Context context) {
	}

	/* Helper Methods */

	@Override
	public final View $(@IdRes int resId) {
		return findViewById(resId);
	}

	@Override
	public final @ColorInt
	int getColor(@ColorRes int colorResId) {
        return ResMan.getColor(getContext(), colorResId);
    }

	@Override
	public final String getString(@StringRes int stringResId) {
        return ResMan.getString(getContext(), stringResId);
    }

	@Override
	public void startActivity(Intent intent) {
	}

	@Override
	public final int sp2px(float sp) {
		return DisplayUtils.sp2px(getContext(), sp);
	}

	@Override
	public final int dp2px(float dp) {
		return DisplayUtils.dp2px(getContext(), dp);
	}

	@Override
	public TelecomManager getTelecomManager() {
		return (TelecomManager) getContext().getSystemService(Context.TELECOM_SERVICE);
	}

	@Override
	public Vibrator getVibrator() {
		return (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
	}

	@Override
	public AlarmManager getAlarmManager() {
		return (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
	}
}
