/*
 * This app is developed by AHMED SLEEM
 *
 * Copyright (c) 2021.  TYP INC. All Rights Reserved
 */

package com.typ.muslim.models;

import com.typ.muslim.features.prays.enums.PrayType;
import com.typ.muslim.features.prays.enums.PrayStatus;
import com.typ.muslim.features.prays.models.Pray;
import com.typ.muslim.managers.AManager;

import java.io.Serializable;

/**
 * Model class represents a single PrayTracker record
 */
public class PrayTrackerRecord implements Serializable {

	private final PrayType pray;
	private final PrayStatus status;
	private final boolean atMosque;
	private final Timestamp prayTime;
	private final Timestamp prayedIn;
	private final Timestamp dayTimestamp;

	public PrayTrackerRecord(PrayType pray, PrayStatus status, boolean atMosque, Timestamp prayTime, Timestamp prayedIn, Timestamp dayTimestamp) {
		this.pray = pray;
		this.status = status;
		this.atMosque = atMosque;
		this.prayTime = prayTime;
		this.prayedIn = prayedIn;
		this.dayTimestamp = dayTimestamp;
	}

	public PrayTrackerRecord(PrayType pray, PrayStatus status, boolean atMosque, long prayTime, long prayedIn, long dayTimestamp) {
		this.pray = pray;
		this.status = status;
		this.atMosque = atMosque;
		this.prayTime = new Timestamp(prayTime);
		this.prayedIn = new Timestamp(prayedIn);
		this.dayTimestamp = new Timestamp(dayTimestamp);
	}

	public static PrayTrackerRecord newMissedPrayRecord(Pray pray) {
		return new PrayTrackerRecord(pray.type,
				PrayStatus.FORGOT,
				false,
				pray.time,
				null,
				pray.time);
	}

	public PrayType getPray() {
		return pray;
	}

	public PrayStatus getStatus() {
		return status;
	}

	public boolean wasAtMosque() {
		return atMosque;
	}

	public Timestamp getPrayTime() {
		return prayTime;
	}

	public Timestamp getPrayedIn() {
		return prayedIn;
	}

	public Timestamp getDayTimestamp() {
		return dayTimestamp;
	}

	public boolean wasPrayed() {
		AManager.log("PrayTrackerRecord", "wasPrayed: %s | %s | %s", pray.name(), status.name(), prayedIn);
		return prayedIn != null && (status == PrayStatus.ON_TIME || status == PrayStatus.DELAYED);
	}

	@Override
	public String toString() {
		return "PrayTrackerRecord{" +
				"pray=" + pray +
				", status=" + status +
				", atMosque=" + atMosque +
				", prayTime=" + prayTime +
				", prayedIn=" + prayedIn +
				", dayTimestamp=" + dayTimestamp +
				'}';
	}
}
