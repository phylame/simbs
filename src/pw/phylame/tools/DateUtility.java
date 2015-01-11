/*
 * Copyright 2015 Peng Wan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pw.phylame.tools;

import java.util.Date;
import java.util.Calendar;

/**
 * Utilities for Date.
 */
public class DateUtility {

    /**
     * Get end date from begin date.
     * @param sDate begin date
     * @param days the interval days
     * @return end date
     */
    public static Date calculateEndDate(Date sDate, int days) {
        Calendar sCalendar = Calendar.getInstance();
        sCalendar.setTime(sDate);
        sCalendar.add(Calendar.DATE, days);
        return sCalendar.getTime();
    }

    /**
     * Get interval between tow dates.
     * @param sDate begin date
     * @param eDate end date
     * @param type interval type, maybe "Y/y": year, "M/m": month, "D/d": day
     * @return interval value
     */
    public static int calculateInterval(Date sDate, Date eDate, String type) {
        int interval = 0;

        Calendar sCalendar = Calendar.getInstance();
        sCalendar.setTime(sDate);
        int sYears = sCalendar.get(Calendar.YEAR);
        int sMonths = sCalendar.get(Calendar.MONTH);
        int sDays = sCalendar.get(Calendar.DAY_OF_YEAR);

        Calendar eCalendar = Calendar.getInstance();
        eCalendar.setTime(eDate);
        int eYears = eCalendar.get(Calendar.YEAR);
        int eMonths = eCalendar.get(Calendar.MONTH);
        int eDays = eCalendar.get(Calendar.DAY_OF_YEAR);

        switch (type) {
            case "Y":
            case "y":
                interval = eYears - sYears;
                if(eMonths < sMonths) {
                    --interval;
                }
                break;
            case "M":
            case "m":
                interval = 12 * (eYears - sYears);
                interval += (eMonths - sMonths);
                break;
            case "D":
            case "d":
                interval = 365 * (eYears - sYears);
                interval += (eDays - sDays);
                // remove days in leap year
                while(sYears < eYears) {
                    if(isLeapYear(sYears)) {
                        --interval;
                    }
                    ++sYears;
                }
                break;
        }
        return interval;
    }

    /**
     * Test the year is leap year or not.
     * @param year the year
     * @return {@code true} if the year is leap year, {@code  false} not leap year
     * */
    public static boolean isLeapYear(int year) {
        return (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0));
    }
}
