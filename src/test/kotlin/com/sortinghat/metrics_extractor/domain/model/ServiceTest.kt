package com.sortinghat.metrics_extractor.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ServiceTest {

    private fun createService() =
        Service(
            name = "account-service",
            responsibility = "",
            module = Module("account-module"),
            system = ServiceBasedSystem("Ifood", "just eat my guy")
        )

    @Test
    fun `should add a new database usage`() {
        val service = createService()
        val db = Database.create("mongo-db", "MongoDB")

        service.addUsage(db, DatabaseAccessType.ReadWrite)

        assertEquals(1, service.databasesUsages.size)
        assertEquals(db, service.databasesUsages.first())
        assertEquals(1, db.usages().size)
        assertEquals(service, db.usages().first())
    }

    @Test
    fun `should expose a new operation`() {
        val service = createService()
        val operation = Operation.fromString("GET /users/{id}")

        service.expose(operation)

        assertEquals(1, service.exposedOperations.size)
        assertEquals(operation, service.exposedOperations.first())
    }

    @Test
    fun `should consume a new operation`() {
        val service = createService()
        val operation = Operation.fromString("GET /users/{id}")

        service.consume(operation)

        assertEquals(1, service.consumedOperations.size)
        assertEquals(operation, service.consumedOperations.first())
    }

    @Test
    fun `should publish to a new message channel`() {
        val service = createService()
        val channel = MessageChannel("OrderCreated")

        service.publishTo(channel)

        assertEquals(1, service.channelsPublishing.size)
        assertEquals(channel, service.channelsPublishing.first())
    }

    @Test
    fun `should subscribe to a new message channel`() {
        val service = createService()
        val channel = MessageChannel("OrderCreated")

        service.subscribeTo(channel)

        assertEquals(1, service.channelsSubscribing.size)
        assertEquals(channel, service.channelsSubscribing.first())
    }
}
