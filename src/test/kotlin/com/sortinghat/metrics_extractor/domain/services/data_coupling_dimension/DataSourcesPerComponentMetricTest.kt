package com.sortinghat.metrics_extractor.domain.services.data_coupling_dimension

import com.sortinghat.metrics_extractor.domain.behaviors.PerComponentResult
import com.sortinghat.metrics_extractor.domain.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DataSourcesPerComponentMetricTest {

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
                database = Database.create("2", "MySQL"),
                role = "principal",
                namespace = "Resource Catalogue DB",
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
    fun `should return the number of data sources each service accesses`() {
        val system = createSystem()
        val services = system.services.toList()
        val expected = mapOf(
            services[0].name to 1,
            services[1].name to 1,
            services[2].name to 1,
        )

        val metricExtractor = DataSourcesPerComponentMetric()
        val actual = (metricExtractor.execute(system) as PerComponentResult).services

        assertEquals(expected, actual)
    }

    @Test
    fun `should return the number of data source each module accesses`() {
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
                database = Database.create("4", "MySQL"),
                role = "cache",
                namespace = "Data Collector cache",
                accessType = DatabaseAccessType.ReadWrite
            )
        )

        val modules = system.modules.toList()
        val expected = mapOf(
            modules[0].name to 1,
            modules[1].name to 1,
            modules[2].name to 2,
        )

        val metricExtractor = DataSourcesPerComponentMetric()
        val actual = (metricExtractor.execute(system) as PerComponentResult).modules

        assertEquals(expected, actual)
    }

    @Test
    fun `should not count services that uses the same database in a module`() {
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
            modules[0].name to 1,
            modules[1].name to 1,
            modules[2].name to 1,
        )

        val metricExtractor = DataSourcesPerComponentMetric()
        val actual = (metricExtractor.execute(system) as PerComponentResult).modules

        assertEquals(expected, actual)
    }
}
