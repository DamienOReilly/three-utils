package org.damienoreilly.threeutils.util

import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

object Utils {

    internal val zoneId = ZoneId.of("Europe/Dublin")

    fun parseDate(date: String): ZonedDateTime {
        return if (date == "In queue") {
            ZonedDateTime.of(2050, 1, 1, 0, 0 ,0 , 0, zoneId)
        } else {
            val dateStr = date.replace("Expires", "").trim()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")
            LocalDate.parse(dateStr, formatter)
                    .plusDays(1) // it expires at the end of the day
                    .atStartOfDay(zoneId)
        }

    }

}