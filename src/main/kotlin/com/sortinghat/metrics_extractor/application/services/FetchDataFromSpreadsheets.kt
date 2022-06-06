package com.sortinghat.metrics_extractor.application.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.sortinghat.metrics_extractor.domain.model.*
import org.springframework.beans.factory.annotation.Value
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@org.springframework.stereotype.Service
class FetchDataFromSpreadsheets {

    @Value("\${google.spreadsheets.api_key}")
    private val apiKey: String = ""

    @Value("\${google.spreadsheets.sheet_id}")
    private val sheetId: String = ""

    private val systemById = mutableMapOf<String, ServiceBasedSystem>()
    private val moduleById = mutableMapOf<String, Module>()
    private val systemByModule = mutableMapOf<Module, ServiceBasedSystem>()
    private val serviceById = mutableMapOf<String, Service>()
    private val databaseById = mutableMapOf<String, Database>()

    fun execute(): Set<Service> {
        val sheetNameToValues = mutableMapOf<String, List<List<String>>>(
            "system" to listOf(),
            "module" to listOf(),
            "service" to listOf(),
            "database" to listOf(),
            "service_database" to listOf(),
            "service_communication" to listOf()
        )

        sheetNameToValues.forEach { sheetNameToValues[it.key] = fetch(it.key) }
        createSystems(sheetNameToValues["system"]!!)
        createModules(sheetNameToValues["module"]!!)
        createServices(sheetNameToValues["service"]!!)
        createDatabases(sheetNameToValues["database"]!!, sheetNameToValues["service_database"]!!)
        addDatabasesUsages(sheetNameToValues["service_database"]!!)
        addServicesCommunications(sheetNameToValues["service_communication"]!!)

        return serviceById.values.toSet()
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

    private fun getUrl(sheetName: String): String {
        return "https://sheets.googleapis.com/v4/spreadsheets/$sheetId/values/'$sheetName'?key=$apiKey"
    }

    private fun createSystems(rawSystems: List<List<String>>) {
        rawSystems.forEach { row ->
            systemById[row[0]] = ServiceBasedSystem(
                name = row[1],
                description = row[2]
            )
        }
    }

    private fun createModules(rawModules: List<List<String>>) {
        rawModules.forEach { row ->
            val module = Module(name = row[2])
            moduleById[row[0]] = module
            systemByModule[module] = systemById[row[1]]!!
        }
    }

    private fun createServices(rawServices: List<List<String>>) {
        rawServices.forEach { row ->
            val module = moduleById[row[1]]!!
            val service = Service(
                name = row[2],
                responsibility = row[3],
                module = module,
                system = systemByModule[module]!!
            )
            row[5]
                .split(",")
                .map { s -> s.trim() }
                .forEach { op ->
                    service.expose(Operation.fromString(op))
                }

            serviceById[row[0]] = service
        }
    }

    private fun createDatabases(rawDatabases: List<List<String>>, rawDatabasesUsages: List<List<String>>) {
        rawDatabasesUsages.forEach { row ->
            val rawDb = rawDatabases.find { db -> db[0] == row[0] }!!
            val database = Database.create(
                namespace = row[4],
                rawModel = rawDb[1].split("-").map { s -> s.trim() }.first()
            )
            databaseById[row[0]] = database
        }
    }

    private fun addDatabasesUsages(rawDatabasesUsages: List<List<String>>) {
        rawDatabasesUsages.forEach { row ->
            val database = databaseById[row[0]]!!
            val service = serviceById[row[1]]!!
            val rawType = row[3].split("and", ",", "/").map { it.trim() }
            val accessType =
                if (rawType.size > 1) DatabaseAccessType.ReadWrite
                else if (listOf("write", "writing").any { rawType.first().lowercase() == it }) DatabaseAccessType.Write
                else DatabaseAccessType.Read

            service.addUsage(database, accessType)
            database.addUsage(service, accessType)
        }
    }

    private fun addServicesCommunications(rawCommunications: List<List<String>>) {
        rawCommunications.forEach { row ->
            val serviceFrom = serviceById[row[0]]!!
            val serviceTo = serviceById[row[1]]!!

            if (row[2].trim() == "0") {
                // async message
                row[3]
                    .split(",")
                    .map { s -> s.trim() }
                    .forEach { topicName ->
                        val channel = MessageChannel(topicName)
                        serviceFrom.publishTo(channel)
                        serviceTo.subscribeTo(channel)
                    }
            } else {
                // sync message
                row[3]
                    .split(",")
                    .map { s -> s.trim() }
                    .forEach { url ->
                        val operation = Operation.fromString(url)
                        serviceTo.expose(operation)
                        serviceFrom.consume(operation)
                    }
            }
        }
    }
}
