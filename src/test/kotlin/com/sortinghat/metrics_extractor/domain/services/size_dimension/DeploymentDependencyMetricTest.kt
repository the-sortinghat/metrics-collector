package com.sortinghat.metrics_extractor.domain.services.size_dimension

import com.sortinghat.metrics_extractor.domain.model.Module
import com.sortinghat.metrics_extractor.domain.model.Service
import com.sortinghat.metrics_extractor.domain.model.ServiceBasedSystem
import com.sortinghat.metrics_extractor.domain.services.ServicesBuilder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DeploymentDependencyMetricTest {

    @Test
    fun `should return zero for a system with no services with deployment dependency`() {
        val services = ServicesBuilder().build()
        val metricExtractor = DeploymentDependencyMetric()

        services.forEach { it.accept(metricExtractor) }

        val (value) = metricExtractor.getResult()

        assertEquals(0, value)
    }

    @Test
    fun `should detect deployment dependencies between services`() {
        val services = ServicesBuilder().build()
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
