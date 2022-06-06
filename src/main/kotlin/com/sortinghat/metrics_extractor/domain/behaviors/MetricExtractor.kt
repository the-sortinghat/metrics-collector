package com.sortinghat.metrics_extractor.domain.behaviors

interface MetricExtractor {
    fun getResult(): ExtractionResult

    fun getMetricDescription(): String
}
