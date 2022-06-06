package com.sortinghat.metrics_extractor.application.services

import com.sortinghat.metrics_extractor.domain.model.ServiceRepository
import com.sortinghat.metrics_extractor.domain.services.ExtractSystemMetrics

@org.springframework.stereotype.Service
class GetSystemDataImpl(private val repository: ServiceRepository) : GetSystemData {

    override fun findAllServicesBySystem(id: String) = repository.findAllBySystem(id)

    override fun findAllSystems() = repository.findAllSystems()

    override fun getMetricsBySystem(id: String) = ExtractSystemMetrics(repository).execute(id)
}
