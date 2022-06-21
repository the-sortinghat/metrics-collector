package com.sortinghat.metrics_extractor.domain.services.size_dimension

import com.sortinghat.metrics_extractor.domain.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SmallestServiceMetricTest {

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
    fun `should return the smallest service in the system`() {
        val services = createServices()
        val expected = "Resource Adaptor (2 operations)"

        val metricExtractor = SmallestServiceMetric()

        services.forEach { service -> service.accept(metricExtractor) }

        val actual = metricExtractor.getResult().value

        assertEquals(expected, actual)
    }

    @Test
    fun `should return the smallest services when there is more than one smallest`() {
        val services = createServices()
        services.add(
            Service(
                name = "Actuator Controller",
                responsibility = "",
                module = Module("Actuator Controller"),
                system = services[0].system,
                exposedOperations = mutableSetOf(
                    Operation(HttpVerb.GET, "/collector/bar/{id}"),
                    Operation(HttpVerb.POST, "/collector/bar")
                )
            )
        )
        val expected = "Actuator Controller, Resource Adaptor (2 operations)"

        val metricExtractor = SmallestServiceMetric()

        services.forEach { service -> service.accept(metricExtractor) }

        val actual = metricExtractor.getResult().value

        assertEquals(expected, actual)
    }

    @Test
    fun `should return operation word in the singular when the smallest service has only one operation`() {
        val services = listOf(
            Service(
                name = "Resource Adaptor",
                responsibility = "",
                module = Module("AdaptorCollector"),
                system = ServiceBasedSystem(name = "InterSCity", description = "InterSCity"),
                exposedOperations = mutableSetOf(
                    Operation(HttpVerb.GET, "/adaptor/bar")
                )
            ),
        )
        val expected = "Resource Adaptor (1 operation)"

        val metricExtractor = SmallestServiceMetric()

        services.forEach { service -> service.accept(metricExtractor) }

        val actual = metricExtractor.getResult().value

        assertEquals(expected, actual)
    }
}
