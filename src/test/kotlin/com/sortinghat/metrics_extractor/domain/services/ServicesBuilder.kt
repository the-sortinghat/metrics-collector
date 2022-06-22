package com.sortinghat.metrics_extractor.domain.services

import com.sortinghat.metrics_extractor.domain.model.*

class ServicesBuilder {
    
    private val services: MutableList<Service>
    
    init {
        val system = ServiceBasedSystem(name = "InterSCity", description = "InterSCity")
        services = mutableListOf(
            Service(
                name = "Resource Adaptor",
                responsibility = "",
                module = Module("Resource Adaptor"),
                system = system
            ),
            Service(
                name = "Resource Catalogue",
                responsibility = "",
                module = Module("Resource Catalogue"),
                system = system
            ),
            Service(
                name = "Data Collector",
                responsibility = "",
                module = Module("Data Collector"),
                system = system
            )
        )
    }
    
    fun addDatabases(): ServicesBuilder {
        val databases = listOf(
            Database.create("Resource Adaptor DB", "MySQL"),
            Database.create("Resource Catalogue DB", "MySQL"),
            Database.create("Data Collector DB", "MySQL")
        )
        services[0].addUsage(databases[0], DatabaseAccessType.ReadWrite)
        services[1].addUsage(databases[1], DatabaseAccessType.ReadWrite)
        services[2].addUsage(databases[2], DatabaseAccessType.ReadWrite)
        return this
    }
    
    fun build(): MutableList<Service> {
        return services
    }
}
