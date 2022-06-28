package com.sortinghat.metrics_extractor.domain.services.async_coupling_dimension

import com.sortinghat.metrics_extractor.domain.model.MessageChannel
import com.sortinghat.metrics_extractor.domain.model.Module
import com.sortinghat.metrics_extractor.domain.model.Service
import com.sortinghat.metrics_extractor.domain.model.ServiceBasedSystem
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MessagesConsumedByEachDependingComponentMetricTest {

    private fun createServices(): MutableList<Service> {
        val system = ServiceBasedSystem(name = "InterSCity", description = "InterSCity")
        return mutableListOf(
            Service(
                name = "Resource Adaptor",
                responsibility = "",
                module = Module("AdaptorCollector"),
                system = system,
                channelsSubscribing = mutableSetOf(
                    MessageChannel("foo"),
                    MessageChannel("bar"),
                )
            ),
            Service(
                name = "Resource Catalogue",
                responsibility = "",
                module = Module("Resource Catalogue"),
                system = system,
                channelsSubscribing = mutableSetOf(
                    MessageChannel("bar"),
                    MessageChannel("baz"),
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
                    MessageChannel("fizzbuzz")
                )
            )
        )
    }

    @Test
    fun `should return the number of different types of messages consumed by each depending service`() {
        val services = createServices()
        val expected = mapOf(
            services[0].name to mapOf(),
            services[1].name to mapOf(),
            services[2].name to mapOf(
                services[0].name to 2,
                services[1].name to 2
            )
        )
        val metricExtractor = MessagesConsumedByEachDependingComponentMetric()

        services.forEach { service -> service.accept(metricExtractor) }

        val actual = metricExtractor.getResult().services

        assertEquals(expected, actual)
    }

    @Test
    fun `should return the number of different types of messages consumed by each depending module`() {
        val services = createServices()
        val modules = services.groupBy { service -> service.module }.keys.toList()
        val expected = mapOf(
            modules[0].name to mapOf(
                modules[1].name to 2,
            ),
            modules[1].name to mapOf(),
        )
        val metricExtractor = MessagesConsumedByEachDependingComponentMetric()

        services.forEach { service -> service.accept(metricExtractor) }

        val actual = metricExtractor.getResult().modules

        assertEquals(expected, actual)
    }
}
