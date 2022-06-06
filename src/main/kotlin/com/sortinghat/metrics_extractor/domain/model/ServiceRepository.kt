package com.sortinghat.metrics_extractor.domain.model

interface ServiceRepository {
    fun findAllBySystem(id: String): Set<Service>

    fun findAllSystems(): Set<ServiceBasedSystem>
}
