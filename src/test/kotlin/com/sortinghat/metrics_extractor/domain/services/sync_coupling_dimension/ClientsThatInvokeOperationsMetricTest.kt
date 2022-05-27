package com.sortinghat.metrics_extractor.domain.services.sync_coupling_dimension

import com.sortinghat.metrics_extractor.domain.behaviors.PerComponentResult
import com.sortinghat.metrics_extractor.domain.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ClientsThatInvokeOperationsMetricTest {

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
    fun `should compute the number of services that invoke the operations of a given service`() {
        val system = createSystem()
        val services = system.services.toList()

        system.addSyncOperation(SyncCommunication(services[0], services[1], Operation.fromString("GET /users")))
        system.addSyncOperation(SyncCommunication(services[1], services[2], Operation.fromString("POST /users")))
        system.addSyncOperation(SyncCommunication(services[1], services[2], Operation.fromString("GET /users/{id}")))

        val expected = mapOf(
            services[0].name to 0,
            services[1].name to 1,
            services[2].name to 1
        )

        val metricExtractor = ClientsThatInvokeOperationsMetric()
        val actual = (metricExtractor.execute(system) as PerComponentResult).services

        assertEquals(expected, actual)
    }

    @Test
    fun `should compute the number of modules that invoke the operations of a given module`() {
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

        val services = system.services.toList()
        val modules = system.modules.toList()

        system.addSyncOperation(SyncCommunication(services[0], services[1], Operation.fromString("GET /users")))
        system.addSyncOperation(SyncCommunication(services[0], services[3], Operation.fromString("PUT /users/{id}")))
        system.addSyncOperation(SyncCommunication(services[1], services[2], Operation.fromString("POST /users")))
        system.addSyncOperation(SyncCommunication(services[1], services[2], Operation.fromString("GET /users/{id}")))
        system.addSyncOperation(SyncCommunication(services[2], services[3], Operation.fromString("DELETE /users/{id}")))
        system.addSyncOperation(SyncCommunication(services[3], services[2], Operation.fromString("GET /foo")))

        val expected = mapOf(
            modules[0].name to 0,
            modules[1].name to 1,
            modules[2].name to 2
        )

        val metricExtractor = ClientsThatInvokeOperationsMetric()
        val actual = (metricExtractor.execute(system) as PerComponentResult).modules

        assertEquals(expected, actual)
    }
}
