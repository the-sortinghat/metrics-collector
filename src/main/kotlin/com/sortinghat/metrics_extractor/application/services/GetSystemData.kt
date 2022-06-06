package com.sortinghat.metrics_extractor.application.services

import com.sortinghat.metrics_extractor.domain.model.Extractions
import com.sortinghat.metrics_extractor.domain.model.Service
import com.sortinghat.metrics_extractor.domain.model.ServiceBasedSystem

interface GetSystemData {
    fun findAllServicesBySystem(id: String): Set<Service>
    fun findAllSystems(): Set<ServiceBasedSystem>
    fun getMetricsBySystem(id: String): Extractions
}
