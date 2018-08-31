package co.netguru.android.carrecognition.common

import junit.framework.Assert.assertEquals
import org.junit.Test

class LimitedListTest {
    @Test
    fun `Should add to limit and not more`() {
        val limitedList = LimitedList<String>(2)
        limitedList.add("first")
        limitedList.add("second")
        limitedList.add("third")

        assertEquals(limitedList[0], "second")
        assertEquals(limitedList[1], "third")
    }

    @Test
    fun `Should keep limit on add all`() {
        val limitedList = LimitedList<String>(2)
        limitedList.addAll(listOf("first", "second", "third"))

        assertEquals(limitedList[0], "second")
        assertEquals(limitedList[1], "third")
    }
}