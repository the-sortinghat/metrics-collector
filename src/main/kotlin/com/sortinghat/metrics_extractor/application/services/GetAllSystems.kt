package com.sortinghat.metrics_extractor.application.services

import com.sortinghat.metrics_extractor.domain.model.System

interface GetAllSystems {
    fun execute(): List<System>
}
