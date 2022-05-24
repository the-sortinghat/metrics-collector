package com.sortinghat.metrics_extractor.domain.services.size_dimension

import com.sortinghat.metrics_extractor.domain.behaviors.NumberResult
import com.sortinghat.metrics_extractor.domain.model.Module
import com.sortinghat.metrics_extractor.domain.model.Service
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import com.sortinghat.metrics_extractor.domain.model.System

class DeploymentDependencyMetricTest {

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
    fun `should return zero for a system with no services with deployment dependency`() {
        val system = createSystem()
        val dependencyMetric = DeploymentDependencyMetric()
        val (value) = dependencyMetric.execute(system) as NumberResult

        assertEquals(0, value)
    }

    @Test
    fun `should detect deployment dependencies between services`() {
        val system = createSystem()
        system.addService(
            Service(
                id = "4",
                name = "Data Collector Outro",
                responsibility = "",
                operations = listOf(),
                module = Module(id = "3", "Data Collector")
            )
        )
        system.addService(
            Service(
                id = "5",
                name = "Data Collector Outro 2",
                responsibility = "",
                operations = listOf(),
                module = Module(id = "3", "Data Collector")
            )
        )
        system.addService(
            Service(
                id = "6",
                name = "Resource Adaptor Outro",
                responsibility = "",
                operations = listOf(),
                module = Module(id = "1", "Resource Adaptor")
            )
        )

        val dependencyMetric = DeploymentDependencyMetric()
        val (value) = dependencyMetric.execute(system) as NumberResult

        assertEquals(5, value)
    }
}
