package com.sortinghat.metrics_extractor.domain.services.async_coupling_dimension

import com.sortinghat.metrics_extractor.domain.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MessagesConsumedMetricTest {

    private fun createServices(): MutableList<Service> {
        val system = ServiceBasedSystem(name = "InterSCity", description = "InterSCity")
        return mutableListOf(
            Service(
                name = "Resource Adaptor",
                responsibility = "",
                module = Module("AdaptorCollector"),
                system = system,
                channelsSubscribing = mutableSetOf(
                    MessageChannel("bar"),
                    MessageChannel("baz"),
                )
            ),
            Service(
                name = "Resource Catalogue",
                responsibility = "",
                module = Module("Resource Catalogue"),
                system = system,
                channelsSubscribing = mutableSetOf(
                    MessageChannel("foo"),
                    MessageChannel("bar"),
                )
            ),
            Service(
                name = "Data Collector",
                responsibility = "",
                module = Module("AdaptorCollector"),
                system = system,
                channelsPublishing = mutableSetOf(
                    MessageChannel("foo"),
                    MessageChannel("bar"),
                    MessageChannel("baz"),
                    MessageChannel("buzz")
                )
            )
        )
    }

    @Test
    fun `should return the number of different types of messages consumed from other services`() {
        val services = createServices()
        val expected = mapOf(
            services[0].name to 0,
            services[1].name to 0,
            services[2].name to 3
        )
        val metricExtractor = MessagesConsumedMetric()

        services.forEach { service -> service.accept(metricExtractor) }

        val actual = metricExtractor.getResult().services

        assertEquals(expected, actual)
    }

    @Test
    fun `should return the number of different types of messages consumed from other modules`() {
        val services = createServices()
        val modules = services.groupBy { service -> service.module }.keys.toList()
        val expected = mapOf(
            modules[0].name to 2,
            modules[1].name to 0,
        )
        val metricExtractor = MessagesConsumedMetric()

        services.forEach { service -> service.accept(metricExtractor) }

        val actual = metricExtractor.getResult().modules

        assertEquals(expected, actual)
    }
}
