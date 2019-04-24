package org.damienoreilly.threeutils.util

import org.damienoreilly.threeutils.util.Utils.zoneId
import org.junit.Test

import org.junit.Assert.*
import org.threeten.bp.ZonedDateTime

class UtilsTest {

    @Test
    fun parseDate() {
        val date = "Expires 27/04/19"
        val actual = Utils.parseDate(date)
        val expected =  ZonedDateTime.of(
                2019,
                4,
                28,
                0,
                0,
                0,
                0,
                zoneId)
        assertEquals(expected, actual)
    }

    @Test
    fun parseDateWhenQueued() {
        val date = "In queue"
        val actual = Utils.parseDate(date)
        val expected =  ZonedDateTime.of(
                2050,
                1,
                1,
                0,
                0,
                0,
                0,
                zoneId)
        assertEquals(expected, actual)
    }
}