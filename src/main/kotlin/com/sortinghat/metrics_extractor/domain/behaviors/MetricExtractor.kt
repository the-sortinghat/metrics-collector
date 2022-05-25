package com.sortinghat.metrics_extractor.domain.behaviors

import com.sortinghat.metrics_extractor.domain.model.System

interface MetricExtractor {
    fun execute(system: System): ExtractionResult

    fun getMetricDescription(): String
}
