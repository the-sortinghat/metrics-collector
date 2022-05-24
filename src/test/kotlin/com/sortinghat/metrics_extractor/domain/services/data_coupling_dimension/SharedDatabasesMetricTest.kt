package com.sortinghat.metrics_extractor.domain.services.data_coupling_dimension

import com.sortinghat.metrics_extractor.domain.behaviors.PerComponentResult
import com.sortinghat.metrics_extractor.domain.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SharedDatabasesMetricTest {
    private fun createSystem(): System {
        val system = System(id = "1", name = "InterSCity", "")
        system.addUsage(
            DatabaseUsage(
                service = Service(
                    id = "1",
                    name = "Resource Adaptor",
                    responsibility = "",
                    operations = listOf(),
                    module = Module(id = "1", "Resource Adaptor")
                ),
                database = Database.create("1", "MySQL"),
                role = "principal",
                namespace = "Resource Adaptor DB",
                accessType = DatabaseAccessType.ReadWrite
            )
        )
        system.addUsage(
            DatabaseUsage(
                service = Service(
                    id = "2",
                    name = "Resource Catalogue",
                    responsibility = "",
                    operations = listOf(),
                    module = Module(id = "2", "Resource Catalog")
                ),
                database = Database.create("1", "MySQL"),
                role = "principal",
                namespace = "Resource Adaptor DB",
                accessType = DatabaseAccessType.ReadWrite
            )
        )
        system.addUsage(
            DatabaseUsage(
                service = Service(
                    id = "3",
                    name = "Data Collector",
                    responsibility = "",
                    operations = listOf(),
                    module = Module(id = "3", "Data Collector")
                ),
                database = Database.create("3", "MySQL"),
                role = "principal",
                namespace = "Data Collector DB",
                accessType = DatabaseAccessType.ReadWrite
            )
        )

        return system
    }

    @Test
    fun `should return the number of data sources that each service shares with the others`() {
        val system = createSystem()
        val services = system.services.toList()
        val expected = mapOf(
            services[0] to 1,
            services[1] to 1,
            services[2] to 0,
        )
        val metricExtractor = SharedDatabasesMetric()
        val actual = (metricExtractor.execute(system) as PerComponentResult).services

        assertEquals(expected, actual)
    }

    @Test
    fun `should return the number of data sources that each module shares with the others`() {
        val system = createSystem()
        val modules = system.modules.toList()
        val expected = mapOf(
            modules[0] to 1,
            modules[1] to 1,
            modules[2] to 0,
        )
        val metricExtractor = SharedDatabasesMetric()
        val actual = (metricExtractor.execute(system) as PerComponentResult).modules

        assertEquals(expected, actual)
    }

    @Test
    fun `should not count shared databases between services in the same module`() {
        val system = createSystem()
        system.addUsage(
            DatabaseUsage(
                service = Service(
                    id = "4",
                    name = "Data Collector Outro",
                    responsibility = "",
                    operations = listOf(),
                    module = Module(id = "3", "Data Collector")
                ),
                database = Database.create("3", "MySQL"),
                role = "principal",
                namespace = "Data Collector DB",
                accessType = DatabaseAccessType.ReadWrite
            )
        )

        val modules = system.modules.toList()
        val expected = mapOf(
            modules[0] to 1,
            modules[1] to 1,
            modules[2] to 0,
        )
        val metricExtractor = SharedDatabasesMetric()
        val actual = (metricExtractor.execute(system) as PerComponentResult).modules

        assertEquals(expected, actual)
    }
}
