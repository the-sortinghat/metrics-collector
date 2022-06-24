package com.sortinghat.metrics_extractor.domain.services.sync_coupling_dimension

import com.sortinghat.metrics_extractor.domain.model.HttpVerb
import com.sortinghat.metrics_extractor.domain.model.Module
import com.sortinghat.metrics_extractor.domain.model.Operation
import com.sortinghat.metrics_extractor.domain.model.Service
import com.sortinghat.metrics_extractor.domain.model.ServiceBasedSystem
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class OperationsInvokedMetricTest {

    private fun createServices(): MutableList<Service> {
        val system = ServiceBasedSystem(name = "InterSCity", description = "InterSCity")
        return mutableListOf(
            Service(
                name = "Resource Adaptor",
                responsibility = "",
                module = Module("AdaptorCollector"),
                system = system,
                consumedOperations = mutableSetOf(
                    Operation(HttpVerb.GET, "/collector/bar/{id}"),
                    Operation(HttpVerb.POST, "/collector/bar"),
                )
            ),
            Service(
                name = "Resource Catalogue",
                responsibility = "",
                module = Module("Resource Catalogue"),
                system = system,
                consumedOperations = mutableSetOf(
                    Operation(HttpVerb.POST, "/collector/bar"),
                    Operation(HttpVerb.PUT, "/collector/bar/{id}"),
                )
            ),
            Service(
                name = "Data Collector",
                responsibility = "",
                module = Module("AdaptorCollector"),
                system = system,
                exposedOperations = mutableSetOf(
                    Operation(HttpVerb.GET, "/collector/bar/{id}"),
                    Operation(HttpVerb.POST, "/collector/bar"),
                    Operation(HttpVerb.PUT, "/collector/bar/{id}"),
                    Operation(HttpVerb.DELETE, "/collector/bar/{id}")
                )
            )
        )
    }

    @Test
    fun `should return the number of different operations invoked from other services`() {
        val services = createServices()
        val expected = mapOf(
            services[0].name to 0,
            services[1].name to 0,
            services[2].name to 3
        )
        val metricExtractor = OperationsInvokedMetric()

        services.forEach { service -> service.accept(metricExtractor) }

        val actual = metricExtractor.getResult().services

        assertEquals(expected, actual)
    }

    @Test
    fun `should return the number of different operations invoked from other modules`() {
        val services = createServices()
        val modules = services.groupBy { service -> service.module }.keys.toList()
        val expected = mapOf(
            modules[0].name to 2,
            modules[1].name to 0,
        )
        val metricExtractor = OperationsInvokedMetric()

        services.forEach { service -> service.accept(metricExtractor) }

        val actual = metricExtractor.getResult().modules

        assertEquals(expected, actual)
    }
}
