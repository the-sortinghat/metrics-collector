package com.sortinghat.metrics_extractor.domain.services.size_dimension

import com.sortinghat.metrics_extractor.domain.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LargestServiceMetricTest {

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
    fun `should return the largest service in the system`() {
        val services = createServices()
        val expected = listOf("Resource Catalogue")

        val metricExtractor = LargestServiceMetric()

        services.forEach { service -> service.accept(metricExtractor) }

        val actual = metricExtractor.getResult().value

        assertEquals(expected, actual)
    }

    @Test
    fun `should return the largest services when there is more than one largest`() {
        val services = createServices()
        services.add(
            Service(
                name = "Actuator Controller",
                responsibility = "",
                module = Module("Actuator Controller"),
                system = services[0].system,
                exposedOperations = mutableSetOf(
                    Operation(HttpVerb.GET, "/collector/bar/{id}"),
                    Operation(HttpVerb.POST, "/collector/bar"),
                    Operation(HttpVerb.PUT, "/collector/bar/{id}"),
                    Operation(HttpVerb.DELETE, "/collector/bar/{id}"),
                    Operation(HttpVerb.PATCH, "/collector/bar/{id}"),
                    Operation(HttpVerb.OPTIONS, "/collector/bar/{id}")
                )
            )
        )
        val expected = listOf("Actuator Controller", "Resource Catalogue")

        val metricExtractor = LargestServiceMetric()

        services.forEach { service -> service.accept(metricExtractor) }

        val actual = metricExtractor.getResult().value

        assertEquals(expected, actual)
    }
}
