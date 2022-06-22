package com.sortinghat.metrics_extractor.domain.services.data_coupling_dimension

import com.sortinghat.metrics_extractor.domain.model.*
import com.sortinghat.metrics_extractor.domain.services.ServicesBuilder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SharedDatabasesMetricTest {

    private fun createServices(): MutableList<Service> {
        val services = ServicesBuilder().build()
        val databases = listOf(
            Database.create("Foo DB", "MySQL"),
            Database.create("Data Collector DB", "MySQL")
        )
        services[0].addUsage(databases[0], DatabaseAccessType.ReadWrite)
        services[1].addUsage(databases[0], DatabaseAccessType.ReadWrite)
        services[2].addUsage(databases[1], DatabaseAccessType.ReadWrite)

        return services
    }

    @Test
    fun `should return the number of data sources that each service shares with the others`() {
        val services = createServices()
        val expected = mapOf(
            services[0].name to 1,
            services[1].name to 1,
            services[2].name to 0,
        )
        val metricExtractor = SharedDatabasesMetric()

        services.forEach { s -> s.accept(metricExtractor) }

        val actual = metricExtractor.getResult().services

        assertEquals(expected, actual)
    }

    @Test
    fun `should return the number of data sources that each module shares with the others`() {
        val services = createServices()
        val modules = services.groupBy { it.module }.keys.toList()
        val expected = mapOf(
            modules[0].name to 1,
            modules[1].name to 1,
            modules[2].name to 0,
        )
        val metricExtractor = SharedDatabasesMetric()

        services.forEach { s -> s.accept(metricExtractor) }

        val actual = metricExtractor.getResult().modules

        assertEquals(expected, actual)
    }

    @Test
    fun `should not count shared databases between services in the same module`() {
        val services = createServices()
        val modules = services.groupBy { it.module }.keys.toList()
        services.add(
            Service(
                name = "Data Collector Outro",
                responsibility = "",
                module = Module("Data Collector"),
                system = ServiceBasedSystem(name = "InterSCity", description = "InterSCity")
            )
        )
        services[3].addUsage(
            Database.create("Data Collector DB", "MySQL"), DatabaseAccessType.ReadWrite
        )

        val expected = mapOf(
            modules[0].name to 1,
            modules[1].name to 1,
            modules[2].name to 0,
        )
        val metricExtractor = SharedDatabasesMetric()

        services.forEach { s -> s.accept(metricExtractor) }

        val actual = metricExtractor.getResult().modules

        assertEquals(expected, actual)
    }
}
