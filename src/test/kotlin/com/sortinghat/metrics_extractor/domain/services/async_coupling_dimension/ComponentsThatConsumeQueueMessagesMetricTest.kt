package com.sortinghat.metrics_extractor.domain.services.async_coupling_dimension

import com.sortinghat.metrics_extractor.domain.model.MessageChannel
import com.sortinghat.metrics_extractor.domain.model.Module
import com.sortinghat.metrics_extractor.domain.model.Service
import com.sortinghat.metrics_extractor.domain.model.ServiceBasedSystem
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ComponentsThatConsumeQueueMessagesMetricTest {

    private fun createServices(): List<Service> {
        val services = listOf(
            Service(
                name = "A",
                responsibility = "",
                module = Module(name = "A"),
                ServiceBasedSystem(name = "mySys", description = "")
            ),
            Service(
                name = "B",
                responsibility = "",
                module = Module(name = "BC"),
                ServiceBasedSystem(name = "mySys", description = "")
            ),
            Service(
                name = "C",
                responsibility = "",
                module = Module(name = "BC"),
                ServiceBasedSystem(name = "mySys", description = "")
            ),
        )

        services[0].subscribeTo(MessageChannel("channel1"))
        services[1].subscribeTo(MessageChannel("channel2"))
        services[2].subscribeTo(MessageChannel("channel3"))

        return services
    }

    @Test
    fun `should return the number of services that consume messages from the queue`() {
        val services = createServices()
        val metricExtractor = ComponentsThatConsumeQueueMessagesMetric()

        services.forEach { service -> service.accept(metricExtractor) }

        val actual = metricExtractor.getResult().services

        assertEquals(3, actual)
    }

    @Test
    fun `should return the number of modules that consume messages from the queue`() {
        val services = createServices()
        val metricExtractor = ComponentsThatConsumeQueueMessagesMetric()

        services.forEach { service -> service.accept(metricExtractor) }

        val actual = metricExtractor.getResult().modules

        assertEquals(2, actual)
    }
}
