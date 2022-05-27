package com.sortinghat.metrics_extractor.application.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.sortinghat.metrics_extractor.domain.model.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import com.sortinghat.metrics_extractor.domain.model.Service as SystemService
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class GetSystemDataByFetchingSpreadsheets : GetSystemData {

    @Value("\${google.spreadsheets.api_key}")
    private val apiKey: String = ""

    @Value("\${google.spreadsheets.sheet_id}")
    private val sheetId: String = ""

    override fun execute(id: String): System {
        val system = parseSystem(fetch("system"), id)
            ?: throw IllegalArgumentException("system with the given id does not exist")

        addModules(fetch("module"), system)
        addServices(fetch("service"), system)
        addDatabasesUsages(fetch("database"), fetch("service_database"), system)
        addServicesCommunications(fetch("service_communication"), system)

        return system
    }

    private fun fetch(sheetName: String): List<List<String>> {
        return try {
            val client = HttpClient.newHttpClient()
            val request = HttpRequest.newBuilder(URI.create(getUrl(sheetName)))
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

    private fun parseSystem(values: List<List<String>>, id: String): System? {
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
        return systems.find { it.id == id }
    }

    private fun addModules(values: List<List<String>>, system: System) {
        values
            .filter { it[1] == system.id }
            .forEach {
                system.addModule(
                    Module(
                        id = it[0],
                        name = it[2]
                    )
                )
            }
    }

    private fun addServices(values: List<List<String>>, system: System) {
        values
            .filter { system.modules.find { module -> module.id == it[1] } != null }
            .forEach {
                system.addService(
                    SystemService(
                        id = it[0],
                        name = it[2],
                        responsibility = it[3],
                        operations = it[5].split(",").map { s -> s.trim() }.map { op -> Operation.fromString(op) },
                        module = system.modules.find { module -> module.id == it[1] }!!
                    )
                )
            }
    }

    private fun addDatabasesUsages(
        valuesDatabases: List<List<String>>,
        valuesUsages: List<List<String>>,
        system: System
    ) {
        valuesUsages
            .filter { system.services.find { service -> service.id == it[1] } != null }
            .forEach {
                val rawDb = valuesDatabases.find { db -> db[0] == it[0] }!!
                val database = Database.create(
                    rawDb[0],
                    rawDb[1].split("-").map { s -> s.trim() }.first()
                )
                val service = system.services.find { service -> service.id == it[1] }!!

                system.addUsage(
                    DatabaseUsage.create(service, database, it[2], it[4], it[3])
                )
            }
    }

    private fun addServicesCommunications(values: List<List<String>>, system: System) {
        values
            .filter {
                system.services.find { service -> service.id == it[0] } != null
                        && system.services.find { service -> service.id == it[1] } != null
            }
            .forEach {
                val from = system.services.find { service -> service.id == it[0] }!!
                val to = system.services.find { service -> service.id == it[1] }!!

                if (it[2] == "0") {
                    it[3]
                        .split(",")
                        .map { s -> s.trim() }
                        .forEach { label ->
                            system.addAsyncOperation(AsyncCommunication(from, to, MessageChannel(label)))
                        }
                } else {
                    it[3]
                        .split(",")
                        .map { s -> s.trim() }
                        .forEach { label ->
                            system.addSyncOperation(SyncCommunication(from, to, Operation.fromString(label)))
                        }
                }
            }
    }

    private fun getUrl(sheetName: String): String {
        return "https://sheets.googleapis.com/v4/spreadsheets/$sheetId/values/'$sheetName'?key=$apiKey"
    }
}
