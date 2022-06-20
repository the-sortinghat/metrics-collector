package com.sortinghat.metrics_extractor.domain.services.size_dimension

import com.sortinghat.metrics_extractor.domain.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class OperationsPerComponentMetricTest {

    private fun createServices(): MutableList<Service> {
        val system = ServiceBasedSystem(name = "InterSCity", description = "InterSCity")
        return mutableListOf(
            Service(
                name = "Resource Adaptor",
                responsibility = "",
                module = Module("AdaptorCollector"),
                system = system,
                exposedOperations = mutableSetOf(
                    Operation(HttpVerb.GET, "/adaptor/bar"),
                    Operation(HttpVerb.POST, "/adaptor/bar")
                )
            ),
            Service(
                name = "Resource Catalogue",
                responsibility = "",
                module = Module("Resource Catalogue"),
                system = system,
                exposedOperations = mutableSetOf(
                    Operation(HttpVerb.GET, "/catalog/bar/{id}"),
                    Operation(HttpVerb.POST, "/catalog/bar"),
                    Operation(HttpVerb.PUT, "/catalog/bar/{id}"),
                    Operation(HttpVerb.DELETE, "/catalog/bar/{id}"),
                    Operation(HttpVerb.GET, "/catalog/foo/{id}"),
                    Operation(HttpVerb.POST, "/catalog/foo")
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
    fun `should return the number of operations each service exposes`() {
        val services = createServices()
        val expected = mapOf(
            services[0].name to 2,
            services[1].name to 6,
            services[2].name to 4,
        )

        val metricExtractor = OperationsPerComponentMetric()

        services.forEach { service -> service.accept(metricExtractor) }

        val actual = metricExtractor.getResult().services

        assertEquals(expected, actual)
    }

    @Test
    fun `should return the number of operations each module exposes`() {
        val services = createServices()
        val modules = services.map { it.module }
        val expected = mapOf(
            modules[0].name to 6,
            modules[1].name to 6,
        )

        val metricExtractor = OperationsPerComponentMetric()

        services.forEach { service -> service.accept(metricExtractor) }

        val actual = metricExtractor.getResult().modules

        assertEquals(expected, actual)
    }
}
