package com.sortinghat.metrics_collector.domain.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DatabaseTest {

    @Test
    fun `throws an exception when database model is not valid`() {
        assertThrows<IllegalArgumentException> { Database.create("1", "something") }
    }
}
