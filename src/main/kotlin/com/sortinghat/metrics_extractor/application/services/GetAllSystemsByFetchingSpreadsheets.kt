package com.sortinghat.metrics_extractor.application.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.sortinghat.metrics_extractor.domain.model.System
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class GetAllSystemsByFetchingSpreadsheets : GetAllSystems {

    @Value("\${google.spreadsheets.api_key}")
    private val apiKey: String = ""

    @Value("\${google.spreadsheets.sheet_id}")
    private val sheetId: String = ""

    override fun execute(): List<System> {
        return parse(fetch())
    }

    private fun fetch(): List<List<String>> {
        return try {
            val client = HttpClient.newHttpClient()
            val request = HttpRequest.newBuilder(
                URI.create(
                    "https://sheets.googleapis.com/v4/spreadsheets/$sheetId/values/'system'?key=$apiKey"
                ))
                .GET()
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            val body = ObjectMapper().readValue<Map<String, Any>>(response.body())
            (body["values"] as List<*>)
                .map { it as List<*> }
                .map { it.map { value -> value.toString() } }
                .drop(1)
        } catch (ex: Exception) {
            listOf()
        }
    }

    private fun parse(values: List<List<String>>): List<System> {
        val systems = mutableSetOf<System>()
        values.forEach {
            systems.add(
                System(
                    id = it[0],
                    name = it[1],
                    description = it[2]
                )
            )
        }
        return systems.toList()
    }
}
