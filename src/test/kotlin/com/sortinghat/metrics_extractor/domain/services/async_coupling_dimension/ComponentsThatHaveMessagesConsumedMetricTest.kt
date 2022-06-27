package com.sortinghat.metrics_extractor.domain.services.async_coupling_dimension

import com.sortinghat.metrics_extractor.domain.model.MessageChannel
import com.sortinghat.metrics_extractor.domain.model.Module
import com.sortinghat.metrics_extractor.domain.model.Service
import com.sortinghat.metrics_extractor.domain.model.ServiceBasedSystem
import com.sortinghat.metrics_extractor.domain.services.ServicesBuilder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ComponentsThatHaveMessagesConsumedMetricTest {

    @Test
    fun `should compute the number of different services from which a given service consumes messages`() {
        val services = ServicesBuilder().build()

        services[0].publishTo(MessageChannel("foo"))
        services[1].publishTo(MessageChannel("bar"))
        services[1].publishTo(MessageChannel("baz"))
        services[1].subscribeTo(MessageChannel("foo"))
        services[2].subscribeTo(MessageChannel("bar"))
        services[2].subscribeTo(MessageChannel("baz"))

        val expected = mapOf(
            services[0].name to 0,
            services[1].name to 1,
            services[2].name to 1
        )

        val metricExtractor = ComponentsThatHaveMessagesConsumedMetric()

        services.forEach { s -> s.accept(metricExtractor) }

        val actual = metricExtractor.getResult().services

        assertEquals(expected, actual)
    }

    @Test
    fun `should compute the number of different modules from which a given module consumes messages`() {
        val services = ServicesBuilder().build()
        services.add(
            Service(
                name = "Data Collector Outro",
                responsibility = "",
                module = Module("Data Collector"),
                system = ServiceBasedSystem(name = "InterSCity", description = "InterSCity")
            )
        )

        val modules = services.groupBy { it.module }.keys.toList()

        services[0].publishTo(MessageChannel("foo"))
        services[0].publishTo(MessageChannel("nice_channel"))
        services[1].publishTo(MessageChannel("bar"))
        services[1].publishTo(MessageChannel("baz"))
        services[2].publishTo(MessageChannel("delete_channel"))
        services[3].publishTo(MessageChannel("GET /foo"))
        services[1].subscribeTo(MessageChannel("foo"))
        services[3].subscribeTo(MessageChannel("nice_channel"))
        services[2].subscribeTo(MessageChannel("bar"))
        services[3].subscribeTo(MessageChannel("baz"))
        services[3].subscribeTo(MessageChannel("delete_channel"))
        services[2].subscribeTo(MessageChannel("GET /foo"))

        val expected = mapOf(
            modules[0].name to 0,
            modules[1].name to 1,
            modules[2].name to 2
        )

        val metricExtractor = ComponentsThatHaveMessagesConsumedMetric()

        services.forEach { s -> s.accept(metricExtractor) }

        val actual = metricExtractor.getResult().modules

        assertEquals(expected, actual)
    }
}
