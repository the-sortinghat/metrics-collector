package com.sortinghat.metrics_extractor.domain.services

import com.sortinghat.metrics_extractor.domain.model.System
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ExtractSystemMetricsTest {

    @Test
    fun `should return a map of metrics and their values for each dimension`() {
        val system = System(id = "1", name = "InterSCity", "")
        val result = ExtractSystemMetrics.execute(system)

        assertEquals(
            setOf("Size", "Data source coupling", "Synchronous coupling", "Asynchronous coupling"),
            result.keys
        )
        assertFalse(result.values.any { it.isEmpty() })
    }
}
