package com.vanthien113.tiancamera.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    const val HH_mm = "HH:mm"
    const val mm_ss = "mm:ss"
    const val HH_mm_dd_MM_yyyy = "HH:mm dd-MM-yyyy"
    const val dd_MM_yyyy_HH_mm = "dd-MM-yyyy HH:mm"
    const val YYYY_MM_dd_HH_mm = "YYYY-MM-dd HH:mm"
    const val EEEE_dd_MM_yyyy = "EEEE, dd/MM/yyyy"
    const val dd_MM_yyyy = "dd/MM/yyyy"
    const val dd_MM_yyyy_line = "dd-MM-yyyy"
    const val dd_MM_yyyy_dot = "dd.MM.yyyy"
    const val HH_mm_dd_MM_yyyy_line = "HH:mm | dd-MM-yyyy"
    const val HH_mm_dd_MM_yyyy_dot = "HH:mm | dd.MM.yyyy"
    const val dd_MM_yyyy_HH_mm_line = "dd-MM-yyyy | HH:mm"

    const val DD_MMM_FORMAT = "dd MMM"
    const val DD_MM_YYYY_FORMAT = "dd/MM/yyyy"

    const val GMT_TIMEZONE = "GMT"
    const val UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    fun getDateFromTimeUTC(date: String): Date? {
        val simpleDateFormat = SimpleDateFormat(UTC_FORMAT, Locale.getDefault())
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return simpleDateFormat.parse(date)
    }

    fun getDateFromTimeGMT(date: String): Date? {
        val simpleDateFormat = SimpleDateFormat(UTC_FORMAT, Locale.getDefault())
        simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT")
        return simpleDateFormat.parse(date)
    }

    fun getDateTimeFormat(date: Date, typeFormat: String): String {
        val simpleDateFormat = SimpleDateFormat(typeFormat, Locale.getDefault())
        return simpleDateFormat.format(date)
    }

    fun getDateTimeFormat(milliSeconds: Long, typeFormat: String): String {
        val simpleDateFormat = SimpleDateFormat(typeFormat, Locale.getDefault())
        val calendar = Calendar.getInstance().apply {
            timeInMillis = milliSeconds
        }
        return simpleDateFormat.format(calendar.time)
    }

    fun getDateTimeFormat(date: Date, typeFormat: String, timeZone: String? = null): String {
        val simpleDateFormat = SimpleDateFormat(typeFormat, Locale.getDefault())

        if (timeZone != null) {
            simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone)
        }

        return simpleDateFormat.format(date)
    }

    fun getDateTimeFormat(date: String, typeFormat: String): Date? {
        val simpleDateFormat = SimpleDateFormat(typeFormat, Locale.getDefault())
        return simpleDateFormat.parse(date)
    }

    fun convertTimeStampToDateString(time: Long, format: String, timeZone: String? = null): String {
        val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
        if (timeZone != null) {
            simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone)
        }
        return simpleDateFormat.format(Date(time))
    }

    fun convertUTCToDateString(date: String, format: String, timeZone: String? = null): String {
        val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
        if (timeZone != null) {
            simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone)
        }
        return simpleDateFormat.format(getDateFromTimeUTC(date))
    }

    fun convertGMTToDateString(date: String, format: String, timeZone: String? = null): String {
        val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
        if (timeZone != null) {
            simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone)
        }
        return simpleDateFormat.format(getDateFromTimeGMT(date))
    }

    fun convertTimeUTCToStringDateTimeFormat(timeUTC: String?, format: String, timeZone: String? = null): String? {
        val date = timeUTC?.let { getDateFromTimeGMT(it) }
        return date?.let { getDateTimeFormat(it, format, timeZone) }
    }

    fun getTimeFromStringTimeFormatGMT(stringTimeGTM: String?): Long {
        return stringTimeGTM?.let {
            getDateFromTimeGMT(stringTimeGTM)?.time ?: System.currentTimeMillis()
        } ?: System.currentTimeMillis()
    }

    fun getCurrentDateTime(): Long { // current millisecond
        return Calendar.getInstance().time.time
    }

    fun getCurrentDate(): Date = Calendar.getInstance().time // current date

    fun convertMillisecondToDuration(mils: Long): String {
        val sec = (mils / 1000) % 60
        val hours = sec / 3600;
        val minutes = (sec % 3600) / 60
        val seconds = sec % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
        return String.format("%02d:%02d", minutes, seconds)
    }

    /**
     * convert mills to String format
     * @return format HH:mm:ss
     */
    fun convertMillisecondToDurationFullFormat(mils: Long): String {
        val sec = (mils / 1000) % 60
        val hours = sec / 3600;
        val minutes = (sec % 3600) / 60
        val seconds = sec % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}