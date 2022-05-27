package com.sortinghat.metrics_extractor.application.services

import com.sortinghat.metrics_extractor.domain.model.System

interface GetSystemData {
    fun execute(id: String): System
}
