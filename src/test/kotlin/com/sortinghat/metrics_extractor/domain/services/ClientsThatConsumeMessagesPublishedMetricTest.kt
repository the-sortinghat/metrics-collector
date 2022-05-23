package com.sortinghat.metrics_extractor.domain.services

import com.sortinghat.metrics_extractor.domain.behaviors.PerComponentResult
import com.sortinghat.metrics_extractor.domain.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ClientsThatConsumeMessagesPublishedMetricTest {

    private fun createSystem(): System {
        val system = System(id = "1", name = "InterSCity", "")
        system.addService(
            Service(
                id = "1",
                name = "Resource Adaptor",
                responsibility = "",
                operations = listOf(),
                module = Module(id = "1", "Resource Adaptor")
            )
        )
        system.addService(
            Service(
                id = "2",
                name = "Resource Catalogue",
                responsibility = "",
                operations = listOf(),
                module = Module(id = "2", "Resource Catalog")
            )
        )
        system.addService(
            Service(
                id = "3",
                name = "Data Collector",
                responsibility = "",
                operations = listOf(),
                module = Module(id = "3", "Data Collector")
            )
        )

        return system
    }

    @Test
    fun `should return 0 for all services and modules when there is no async communications`() {
        val system = createSystem()
        val expected = PerComponentResult(
            modules = system.modules.associateWith { 0 },
            services = system.services.associateWith { 0 }
        )
        val metricExtractor = ClientsThatConsumeMessagesPublishedMetric()
        val actual = metricExtractor.execute(system)

        assertEquals(expected, actual)
    }

    @Test
    fun `should compute all async messages that every service publishes in`() {
        val system = createSystem()
        val services = system.services.toList()

        system.addAsyncOperation(AsyncCommunication(services[0], services[1], MessageChannel("Topic1")))
        system.addAsyncOperation(AsyncCommunication(services[0], services[2], MessageChannel("Topic2")))
        system.addAsyncOperation(AsyncCommunication(services[1], services[2], MessageChannel("Topic3")))

        val expected = mapOf(
            services[0] to 2,
            services[1] to 1,
            services[2] to 0
        )

        val metricExtractor = ClientsThatConsumeMessagesPublishedMetric()
        val actual = (metricExtractor.execute(system) as PerComponentResult).services

        assertEquals(expected, actual)
    }

    @Test
    fun `should sum the number of async messages that services inside every module publish in`() {
        val system = createSystem()
        system.addService(
            Service(
                id = "4",
                name = "Resource Adaptor Outro",
                responsibility = "",
                operations = listOf(),
                module = Module(id = "1", "Resource Adaptor")
            )
        )

        val services = system.services.toList()
        val modules = system.modules.toList()

        system.addAsyncOperation(AsyncCommunication(services[0], services[1], MessageChannel("Topic1")))
        system.addAsyncOperation(AsyncCommunication(services[0], services[2], MessageChannel("Topic2")))
        system.addAsyncOperation(AsyncCommunication(services[1], services[2], MessageChannel("Topic3")))
        system.addAsyncOperation(AsyncCommunication(services[3], services[2], MessageChannel("Topic4")))

        val expected = mapOf(
            modules[0] to 3,
            modules[1] to 1,
            modules[2] to 0
        )

        val metricExtractor = ClientsThatConsumeMessagesPublishedMetric()
        val actual = (metricExtractor.execute(system) as PerComponentResult).modules

        assertEquals(expected, actual)
    }

    @Test
    fun `should not compute async messages between services in the same module`() {
        val system = createSystem()
        system.addService(
            Service(
                id = "4",
                name = "Resource Adaptor Outro",
                responsibility = "",
                operations = listOf(),
                module = Module(id = "1", "Resource Adaptor")
            )
        )

        val services = system.services.toList()
        val modules = system.modules.toList()

        system.addAsyncOperation(AsyncCommunication(services[0], services[1], MessageChannel("Topic1")))
        system.addAsyncOperation(AsyncCommunication(services[0], services[2], MessageChannel("Topic2")))
        system.addAsyncOperation(AsyncCommunication(services[1], services[2], MessageChannel("Topic3")))
        system.addAsyncOperation(AsyncCommunication(services[3], services[0], MessageChannel("Topic4")))

        val expected = mapOf(
            modules[0] to 2,
            modules[1] to 1,
            modules[2] to 0
        )

        val metricExtractor = ClientsThatConsumeMessagesPublishedMetric()
        val actual = (metricExtractor.execute(system) as PerComponentResult).modules

        assertEquals(expected, actual)
    }
}
