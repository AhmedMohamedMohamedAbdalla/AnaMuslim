/*
 * This app is developed by AHMED SLEEM
 *
 * Copyright (c) 2021.  TYP INC. All Rights Reserved
 */

package com.typ.muslim.models.quran;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.typ.muslim.managers.QuranProvider;

import java.io.Serializable;

public class QuranAyah implements Serializable {

	private final @IntRange(from = 1, to = 114) int surah;
	private final @IntRange(from = 1, to = 286) int number;
	private @Nullable String content;

	public QuranAyah(@IntRange(from = 1, to = 286) int number, @IntRange(from = 1, to = 114) int surah, @Nullable String content) {
		this.number = number;
		this.surah = surah;
		this.content = content;
	}

	public static QuranAyah of(Context c, @IntRange(from = 1, to = 286) int ayahNumber, @IntRange(from = 1, to = 114) int surahNumber) {
		return QuranProvider.getAyahInSurah(c, ayahNumber, surahNumber);
	}

	public int getNumber() {
		return number;
	}

	@NonNull
	public String getContent(Context context) {
		if (TextUtils.isEmpty(content) && context != null) {
			this.content = QuranProvider.getAyahInSurah(context, number, surah).content;
		}
		return content;
	}

	public int getSurahNumber() {
		return surah;
	}


	public QuranSurah getSurah() {
		return QuranProvider.getSurah(this.surah);
	}

	@Override
	public String toString() {
		return "QuranAyah{" +
		       "number=" + number +
		       "surah=" + surah +
		       ", content='" + content + '\'' +
		       '}';
	}

	public String toString(Context context) {
		return "QuranAyah{" +
		       "number=" + number +
		       "surah=" + surah +
		       ", content='" + getContent(context) + '\'' +
		       '}';
	}

}
