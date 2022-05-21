package com.sortinghat.metrics_collector.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OperationTest {

    @Test
    fun `splits the url into http verb and uri correctly`() {
        val operation = Operation.fromString("GET /users/{id}")
        assertEquals(HttpVerb.GET, operation.verb)
        assertEquals("/users/{id}", operation.uri)
    }

    @Test
    fun `throws an exception when http verb is not valid`() {
        assertThrows<IllegalArgumentException> { Operation.fromString("SOME /users/{id}") }
    }

    @Test
    fun `throws an exception when url is not valid`() {
        assertThrows<IllegalArgumentException> { Operation.fromString("SOME/users/{id}") }
    }
}
