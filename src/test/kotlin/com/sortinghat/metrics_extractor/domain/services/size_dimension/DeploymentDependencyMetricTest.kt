package com.sortinghat.metrics_extractor.domain.services.size_dimension

import com.sortinghat.metrics_extractor.domain.model.Module
import com.sortinghat.metrics_extractor.domain.model.Service
import com.sortinghat.metrics_extractor.domain.model.ServiceBasedSystem
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DeploymentDependencyMetricTest {

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
    fun `should return zero for a system with no services with deployment dependency`() {
        val services = createServices()
        val metricExtractor = DeploymentDependencyMetric()

        services.forEach { it.accept(metricExtractor) }

        val (value) = metricExtractor.getResult()

        assertEquals(0, value)
    }

    @Test
    fun `should detect deployment dependencies between services`() {
        val services = createServices()
        services.add(
            Service(
                name = "Data Collector Outro",
                responsibility = "",
                module = Module(  "Data Collector"),
                system = ServiceBasedSystem(name = "InterSCity", description = "InterSCity")
            )
        )
        services.add(
            Service(
                name = "Data Collector Outro 2",
                responsibility = "",
                module = Module(  "Data Collector"),
                system = ServiceBasedSystem(name = "InterSCity", description = "InterSCity")
            )
        )
        services.add(
            Service(
                name = "Resource Adaptor Outro",
                responsibility = "",
                module = Module(  "Resource Adaptor"),
                system = ServiceBasedSystem(name = "InterSCity", description = "InterSCity")
            )
        )

        val metricExtractor = DeploymentDependencyMetric()

        services.forEach { it.accept(metricExtractor) }

        val (value) = metricExtractor.getResult()

        assertEquals(5, value)
    }
}
