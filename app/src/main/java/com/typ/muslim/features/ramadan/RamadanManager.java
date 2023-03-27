/*
 * This app is developed by AHMED SLEEM
 *
 * Copyright (c) 2021.  TYP INC. All Rights Reserved
 */

package com.typ.muslim.features.ramadan;

import static com.typ.muslim.core.ummelqura.UmmalquraCalendar.RAMADAN;

import com.typ.muslim.features.calendar.HijriCalendar;
import com.typ.muslim.features.calendar.models.HijriDate;
import com.typ.muslim.features.ramadan.models.Ramadan;

public class RamadanManager {

    /**
     * Checks whether current hijri month is Ramadan or not
     *
     * @return {@code true} if only current hijri month is Ramadan. {@code false} if not.
     */
    public static boolean isInRamadan() {
        return HijriCalendar.getToday().getMonth() == RAMADAN;
    }

    public static Ramadan getThisYearRamadan() {
        HijriDate todayHijri = HijriCalendar.getToday();
        final int ramadanLength = HijriCalendar.lengthOfMonth(todayHijri.getYear(), RAMADAN + 1);
        return new Ramadan(new HijriDate(todayHijri.getYear(), RAMADAN, 1).toGregorian(),
                new HijriDate(todayHijri.getYear(), RAMADAN, ramadanLength).toGregorian());
    }

    public static Ramadan getNextRamadan() {
        // todo: Check if in ramadan
        HijriDate todayHijri = HijriCalendar.getToday();
        if (todayHijri.getMonth() > RAMADAN) {
            final int ramadanLength = HijriCalendar.lengthOfMonth(todayHijri.getYear() + 1, RAMADAN);
            return new Ramadan(new HijriDate(todayHijri.getYear() + 1, RAMADAN, 1).toGregorian(),
                    new HijriDate(todayHijri.getYear() + 1, RAMADAN, ramadanLength).toGregorian());
        }
        return getThisYearRamadan(); // return this year ramadan.
    }

    public static boolean isRamadanThisYearPassed() {
        return !isInRamadan() && !getNextRamadan().isPassed();
    }

}
