package com.sortinghat.metrics_extractor.domain.services.data_coupling_dimension

import com.sortinghat.metrics_extractor.domain.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DatabasesByAccessTypeMetricTest {

    private fun createServices(): MutableList<Service> {
        val system = ServiceBasedSystem(name = "InterSCity", description = "InterSCity")
        val services = mutableListOf(
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

        return services
    }

    @Test
    fun `should return the number of data sources that each component performs read-only operations`() {
        val services = createServices()
        val modules = services.map { s -> s.module }.toSet().toList()
        val databases = listOf(
            Database.create("Resource Adaptor DB", "MySQL"),
            Database.create("Resource Catalogue DB", "MySQL"),
            Database.create("Data Collector DB", "MySQL")
        )

        services[0].addUsage(databases[0], DatabaseAccessType.Read)
        services[1].addUsage(databases[1], DatabaseAccessType.ReadWrite)
        services[2].addUsage(databases[2], DatabaseAccessType.Read)

        val expectedServices = mapOf(
            services[0].name to 1,
            services[1].name to 0,
            services[2].name to 1,
        )
        val expectedModules = mapOf(
            modules[0].name to 1,
            modules[1].name to 0,
            modules[2].name to 1,
        )

        val metricExtractor = DatabasesByAccessTypeMetric(DatabaseAccessType.Read)

        services.forEach { service -> service.accept(metricExtractor) }

        val actual = metricExtractor.getResult()

        assertEquals(expectedModules, actual.modules)
        assertEquals(expectedServices, actual.services)
    }

    @Test
    fun `should return the number of data sources that each component performs write-only operations`() {
        val services = createServices()
        val modules = services.map { s -> s.module }.toSet().toList()
        val databases = listOf(
            Database.create("Resource Adaptor DB", "MySQL"),
            Database.create("Resource Catalogue DB", "MySQL"),
            Database.create("Data Collector DB", "MySQL")
        )

        services[0].addUsage(databases[0], DatabaseAccessType.Write)
        services[1].addUsage(databases[1], DatabaseAccessType.Read)
        services[2].addUsage(databases[2], DatabaseAccessType.Write)

        val expectedServices = mapOf(
            services[0].name to 1,
            services[1].name to 0,
            services[2].name to 1,
        )
        val expectedModules = mapOf(
            modules[0].name to 1,
            modules[1].name to 0,
            modules[2].name to 1,
        )

        val metricExtractor = DatabasesByAccessTypeMetric(DatabaseAccessType.Write)

        services.forEach { service -> service.accept(metricExtractor) }

        val actual = metricExtractor.getResult()

        assertEquals(expectedModules, actual.modules)
        assertEquals(expectedServices, actual.services)
    }

    @Test
    fun `should return the number of data sources that each component performs read and write operations`() {
        val services = createServices()
        val modules = services.map { s -> s.module }.toSet().toList()
        val databases = listOf(
            Database.create("Resource Adaptor DB", "MySQL"),
            Database.create("Resource Catalogue DB", "MySQL"),
            Database.create("Data Collector DB", "MySQL")
        )

        services[0].addUsage(databases[0], DatabaseAccessType.ReadWrite)
        services[1].addUsage(databases[1], DatabaseAccessType.Read)
        services[2].addUsage(databases[2], DatabaseAccessType.Write)

        val expectedServices = mapOf(
            services[0].name to 1,
            services[1].name to 0,
            services[2].name to 0,
        )
        val expectedModules = mapOf(
            modules[0].name to 1,
            modules[1].name to 0,
            modules[2].name to 0,
        )

        val metricExtractor = DatabasesByAccessTypeMetric(DatabaseAccessType.ReadWrite)

        services.forEach { service -> service.accept(metricExtractor) }

        val actual = metricExtractor.getResult()

        assertEquals(expectedModules, actual.modules)
        assertEquals(expectedServices, actual.services)
    }

    @Test
    fun `should not count shared databases in the same module more than twice`() {
        val services = createServices()
        services.add(
            Service(
                name = "Data Collector Outro",
                responsibility = "",
                module = Module("Data Collector"),
                system = ServiceBasedSystem(name = "InterSCity", description = "InterSCity")
            )
        )

        val modules = services.map { s -> s.module }.toSet().toList()
        val databases = listOf(
            Database.create("Resource Adaptor DB", "MySQL"),
            Database.create("Resource Catalogue DB", "MySQL"),
            Database.create("Data Collector DB", "MySQL")
        )

        services[0].addUsage(databases[0], DatabaseAccessType.ReadWrite)
        services[1].addUsage(databases[1], DatabaseAccessType.ReadWrite)
        services[2].addUsage(databases[2], DatabaseAccessType.ReadWrite)
        services[3].addUsage(databases[2], DatabaseAccessType.ReadWrite)

        val expectedModules = mapOf(
            modules[0].name to 1,
            modules[1].name to 1,
            modules[2].name to 1,
        )

        val metricExtractor = DatabasesByAccessTypeMetric(DatabaseAccessType.ReadWrite)

        services.forEach { service -> service.accept(metricExtractor) }

        val actual = metricExtractor.getResult()

        assertEquals(expectedModules, actual.modules)
    }
}
