package com.sortinghat.metrics_extractor.domain.services

import com.sortinghat.metrics_extractor.domain.behaviors.ExtractionResult
import com.sortinghat.metrics_extractor.domain.behaviors.MetricExtractor
import com.sortinghat.metrics_extractor.domain.behaviors.NumberResult
import com.sortinghat.metrics_extractor.domain.model.System

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
}
