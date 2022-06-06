package com.sortinghat.metrics_extractor.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DatabaseTest {

    @Test
    fun `throws an exception when database model is not valid`() {
        assertThrows<IllegalArgumentException> { Database.create("foo", "bar") }
    }

    @Test
    fun `should add a new service usage`() {
        val db = Database.create("mongo-db", "MongoDB")
        val service = Service(
            name = "account-service",
            responsibility = "",
            module = Module("account-module"),
            system = ServiceBasedSystem("Ifood", "just eat my guy")
        )

        db.addUsage(service, DatabaseAccessType.ReadWrite)

        assertEquals(1, db.usages().size)
        assertEquals(service, db.usages().first())
        assertEquals(1, service.databasesUsages.size)
        assertEquals(db, service.databasesUsages.first())
    }
}
