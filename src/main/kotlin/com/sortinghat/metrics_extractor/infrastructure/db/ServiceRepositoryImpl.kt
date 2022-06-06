package com.sortinghat.metrics_extractor.infrastructure.db

import com.sortinghat.metrics_extractor.application.services.FetchDataFromSpreadsheets
import com.sortinghat.metrics_extractor.domain.model.Service
import com.sortinghat.metrics_extractor.domain.model.ServiceBasedSystem
import com.sortinghat.metrics_extractor.domain.model.ServiceRepository
import org.springframework.stereotype.Component

@Component
class ServiceRepositoryImpl(private val fetchDataFromSpreadsheets: FetchDataFromSpreadsheets) : ServiceRepository {

    override fun findAllBySystem(id: String): Set<Service> {
        val services = fetchDataFromSpreadsheets.execute()
        return services.filter { service -> service.system.name == id }.toSet()
    }

    override fun findAllSystems(): Set<ServiceBasedSystem> {
        val services = fetchDataFromSpreadsheets.execute()
        return services.map { service -> service.system }.toSet()
    }
}
