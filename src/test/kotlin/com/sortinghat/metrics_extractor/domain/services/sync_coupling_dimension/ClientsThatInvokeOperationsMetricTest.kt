package com.sortinghat.metrics_extractor.domain.services.sync_coupling_dimension

import com.sortinghat.metrics_extractor.domain.model.Module
import com.sortinghat.metrics_extractor.domain.model.Operation
import com.sortinghat.metrics_extractor.domain.model.Service
import com.sortinghat.metrics_extractor.domain.model.ServiceBasedSystem
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ClientsThatInvokeOperationsMetricTest {

    private fun createServices(): MutableList<Service> {
        val system = ServiceBasedSystem(name = "InterSCity", description = "InterSCity")
        return mutableListOf(
            Service(
                name = "Resource Adaptor",
                responsibility = "",
                module = Module("Resource Adaptor"),
                system = system
            ),
            Service(
                name = "Resource Catalogue",
                responsibility = "",
                module = Module("Resource Catalogue"),
                system = system
            ),
            Service(
                name = "Data Collector",
                responsibility = "",
                module = Module("Data Collector"),
                system = system
            )
        )
    }

    @Test
    fun `should compute the number of different services that invoke the operations of a given service`() {
        val services = createServices()

        services[0].expose(Operation.fromString("GET /users"))
        services[1].expose(Operation.fromString("POST /users"))
        services[1].expose(Operation.fromString("GET /users/{id}"))
        services[1].consume(Operation.fromString("GET /users"))
        services[2].consume(Operation.fromString("POST /users"))
        services[2].consume(Operation.fromString("GET /users/{id}"))

        val expected = mapOf(
            services[0].name to 1,
            services[1].name to 1,
            services[2].name to 0
        )

        val metricExtractor = ClientsThatInvokeOperationsMetric()

        services.forEach { s -> s.accept(metricExtractor) }

        val actual = metricExtractor.getResult().services

        assertEquals(expected, actual)
    }

    @Test
    fun `should compute the number of different modules that invoke the operations of a given module`() {
        val services = createServices()
        services.add(
            Service(
                name = "Data Collector Outro",
                responsibility = "",
                module = Module("Data Collector"),
                system = ServiceBasedSystem(name = "InterSCity", description = "InterSCity")
            )
        )

        val modules = services.groupBy { it.module }.keys.toList()

        services[0].expose(Operation.fromString("GET /users"))
        services[0].expose(Operation.fromString("PUT /users/{id}"))
        services[1].expose(Operation.fromString("POST /users"))
        services[1].expose(Operation.fromString("GET /users/{id}"))
        services[2].expose(Operation.fromString("DELETE /users/{id}"))
        services[3].expose(Operation.fromString("GET /foo"))
        services[1].consume(Operation.fromString("GET /users"))
        services[3].consume(Operation.fromString("PUT /users/{id}"))
        services[2].consume(Operation.fromString("POST /users"))
        services[3].consume(Operation.fromString("GET /users/{id}"))
        services[3].consume(Operation.fromString("DELETE /users/{id}"))
        services[2].consume(Operation.fromString("GET /foo"))

        val expected = mapOf(
            modules[0].name to 2,
            modules[1].name to 1,
            modules[2].name to 0
        )

        val metricExtractor = ClientsThatInvokeOperationsMetric()

        services.forEach { s -> s.accept(metricExtractor) }

        val actual = metricExtractor.getResult().modules

        assertEquals(expected, actual)
    }
}
