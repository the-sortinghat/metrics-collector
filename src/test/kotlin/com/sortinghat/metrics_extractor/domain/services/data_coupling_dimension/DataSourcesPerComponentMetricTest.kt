package com.sortinghat.metrics_extractor.domain.services.data_coupling_dimension

import com.sortinghat.metrics_extractor.domain.model.*
import com.sortinghat.metrics_extractor.domain.services.ServicesBuilder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DataSourcesPerComponentMetricTest {

    @Test
    fun `should return the number of data sources each service accesses`() {
        val services = ServicesBuilder().addDatabases().build()
        val expected = mapOf(
            services[0].name to 1,
            services[1].name to 1,
            services[2].name to 1,
        )

        val metricExtractor = DataSourcesPerComponentMetric()

        services.forEach { service -> service.accept(metricExtractor) }

        val actual = metricExtractor.getResult().services

        assertEquals(expected, actual)
    }

    @Test
    fun `should return the number of data source each module accesses`() {
        val services = ServicesBuilder().addDatabases().build()
        services.add(
            Service(
                name = "Data Collector Outro",
                responsibility = "",
                module = Module("Data Collector"),
                system = ServiceBasedSystem(name = "InterSCity", description = "InterSCity")
            )
        )

        services[3].addUsage(Database.create("Data Collector cache", "MySQL"), DatabaseAccessType.ReadWrite)

        val modules = services.map { it.module }
        val expected = mapOf(
            modules[0].name to 1,
            modules[1].name to 1,
            modules[2].name to 2,
        )

        val metricExtractor = DataSourcesPerComponentMetric()

        services.forEach { service -> service.accept(metricExtractor) }

        val actual = metricExtractor.getResult().modules

        assertEquals(expected, actual)
    }

    @Test
    fun `should not count services that uses the same database in a module`() {
        val services = ServicesBuilder().addDatabases().build()
        services.add(
            Service(
                name = "Data Collector Outro",
                responsibility = "",
                module = Module("Data Collector"),
                system = ServiceBasedSystem(name = "InterSCity", description = "InterSCity")
            )
        )

        services[3].addUsage(Database.create("Data Collector DB", "MySQL"), DatabaseAccessType.ReadWrite)

        val modules = services.map { it.module }
        val expected = mapOf(
            modules[0].name to 1,
            modules[1].name to 1,
            modules[2].name to 1,
        )

        val metricExtractor = DataSourcesPerComponentMetric()

        services.forEach { service -> service.accept(metricExtractor) }

        val actual = metricExtractor.getResult().modules

        assertEquals(expected, actual)
    }
}
