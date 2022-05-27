package com.sortinghat.metrics_extractor.application.services

import com.sortinghat.metrics_extractor.domain.behaviors.ExtractionResult
import com.sortinghat.metrics_extractor.domain.services.ExtractSystemMetrics
import org.springframework.stereotype.Service

@Service
class GetSystemMetrics(private val getSystemData: GetSystemData) {

    fun execute(id: String): Map<String, Map<String, ExtractionResult>> {
        val system = getSystemData.execute(id)
        return ExtractSystemMetrics.execute(system)
    }
}
