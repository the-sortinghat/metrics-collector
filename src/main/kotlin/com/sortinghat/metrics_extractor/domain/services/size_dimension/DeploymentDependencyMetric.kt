package com.sortinghat.metrics_extractor.domain.services.size_dimension

import com.sortinghat.metrics_extractor.domain.behaviors.ExtractionResult
import com.sortinghat.metrics_extractor.domain.behaviors.MetricExtractor
import com.sortinghat.metrics_extractor.domain.behaviors.NumberResult
import com.sortinghat.metrics_extractor.domain.model.System

/**
 * Number of services with deployment dependency
 * Dimension: Size
 */
class DeploymentDependencyMetric: MetricExtractor {
    override fun execute(system: System): ExtractionResult {
        val value = system.services
            .groupBy { it.module }
            .values
            .filter { it.size > 1 }
            .flatten()
            .size

        return NumberResult(value)
    }

    override fun getMetricDescription(): String {
        return "Number of services with deployment dependency"
    }
}
