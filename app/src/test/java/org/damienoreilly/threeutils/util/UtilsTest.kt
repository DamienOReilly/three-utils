package org.damienoreilly.threeutils.util

import org.damienoreilly.threeutils.util.Utils.zoneId
import org.junit.Test

import org.junit.Assert.*
import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime

class UtilsTest {

    @Test
    fun parseDateTest() {
        val date = "Expires 27/04/19"
        val actual = Utils.parseDate(date)
        val expected =  ZonedDateTime.of(
                2019, 4, 28, 0, 0, 0, 0, zoneId)
        assertEquals(expected, actual)
    }

    @Test
    fun parseDateWhenQueuedTest() {
        val date = "In queue"
        val actual = Utils.parseDate(date)
        val expected = null
        assertEquals(expected, actual)
    }

    @Test
    fun getDelayTest() {
        /**
         * It is currently 2019-04-26 18:00:00. My internet expires at 2019-04-29 00:00:00.
         * Using a 4 hours advanced notice, I expect to have a delay result of 180,000
         * (2019-04-29 00:00:00 - 4 hours) - 2019-04-26 18:00:00 = 180,000 seconds
         */
        val expires =  ZonedDateTime.of(
                2019, 4, 29, 0, 0, 0, 0, zoneId)
        val now =  ZonedDateTime.of(
                2019, 4, 26, 18, 0, 0, 0, zoneId)

        val actual = Utils.getDelay(expires, now, Duration.ofHours(4))
        val expected = Duration.ofSeconds(180_000)

        assertEquals(expected, actual)

    }
}