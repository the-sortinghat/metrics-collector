package com.sortinghat.metrics_extractor.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SystemTest {

    @Test
    fun `add service`() {
        val module = Module(id = "1", name = "resource-catalog-module")
        val service = Service(
            id = "1",
            name = "resource-catalog",
            responsibility = "",
            operations = listOf(),
            module = module
        )
        val system = System(id = "1", name = "InterSCity", description = "")
        system.addService(service)

        assertEquals(1, system.modules.size)
        assertEquals(1, system.services.size)
        assertEquals(module, system.modules.first())
        assertEquals(service, system.services.first())
    }

    @Test
    fun `add module`() {
        val module = Module(id = "1", name = "resource-catalog-module")
        val service = Service(
            id = "1",
            name = "resource-catalog",
            responsibility = "",
            operations = listOf(),
            module = module
        )
        val system = System(id = "1", name = "InterSCity", description = "")

        module.addService(service)
        system.addModule(module)

        assertEquals(1, system.modules.size)
        assertEquals(1, system.services.size)
        assertEquals(module, system.modules.first())
        assertEquals(service, system.services.first())
    }

    @Test
    fun `add database`() {
        val database = Database.create("1", "MySQL")
        val system = System(id = "1", name = "InterSCity", description = "")

        system.addDatabase(database)
        system.addDatabase(database)

        assertEquals(1, system.databases.size)
        assertEquals(database, system.databases.first())
    }

    @Test
    fun `add database usage`() {
        val module = Module(id = "1", name = "resource-catalog-module")
        val service = Service(
            id = "1",
            name = "resource-catalog",
            responsibility = "",
            operations = listOf(),
            module = module
        )
        val database = Database.create("1", "MySQL")
        val usage = DatabaseUsage(
            service = service,
            database = database,
            namespace = "resource-catalog-db",
            role = "cache",
            accessType = DatabaseAccessType.ReadWrite
        )
        val system = System(id = "1", name = "InterSCity", description = "")

        system.addUsage(usage)

        assertEquals(1, system.modules.size)
        assertEquals(1, system.services.size)
        assertEquals(1, system.databases.size)
        assertEquals(1, system.usages.size)
        assertEquals(module, system.modules.first())
        assertEquals(service, system.services.first())
        assertEquals(database, system.databases.first())
        assertEquals(usage, system.usages.first())
    }

    @Test
    fun `add sync operation`() {
        val module = Module(id = "1", name = "resource-catalog-module")
        val serviceA = Service(
            id = "1",
            name = "resource-catalog",
            responsibility = "",
            operations = listOf(),
            module = module
        )
        val serviceB = Service(
            id = "2",
            name = "resource-adaptor",
            responsibility = "",
            operations = listOf(),
            module = module
        )
        val syncOperation = SyncCommunication(serviceA, serviceB, Operation(HttpVerb.GET, "/users/{id}"))
        val system = System(id = "1", name = "InterSCity", description = "")

        system.addSyncOperation(syncOperation)

        assertEquals(1, system.modules.size)
        assertEquals(2, system.services.size)
        assertEquals(1, system.syncOperations.size)
        assertEquals(module, system.modules.first())
        assertEquals(serviceA, system.services.first())
        assertEquals(serviceB, system.services.last())
        assertEquals(syncOperation, system.syncOperations.first())
    }

    @Test
    fun `add async operation`() {
        val module = Module(id = "1", name = "resource-catalog-module")
        val serviceA = Service(
            id = "1",
            name = "resource-catalog",
            responsibility = "",
            operations = listOf(),
            module = module
        )
        val serviceB = Service(
            id = "2",
            name = "resource-adaptor",
            responsibility = "",
            operations = listOf(),
            module = module
        )
        val asyncOperation = AsyncCommunication(serviceA, serviceB, MessageChannel("AdaptorNotifier"))
        val system = System(id = "1", name = "InterSCity", description = "")

        system.addAsyncOperation(asyncOperation)

        assertEquals(1, system.modules.size)
        assertEquals(2, system.services.size)
        assertEquals(1, system.asyncOperations.size)
        assertEquals(module, system.modules.first())
        assertEquals(serviceA, system.services.first())
        assertEquals(serviceB, system.services.last())
        assertEquals(asyncOperation, system.asyncOperations.first())
    }
}
