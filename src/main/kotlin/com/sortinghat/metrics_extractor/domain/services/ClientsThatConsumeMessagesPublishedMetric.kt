package com.sortinghat.metrics_extractor.domain.services

import com.sortinghat.metrics_extractor.domain.behaviors.ExtractionResult
import com.sortinghat.metrics_extractor.domain.behaviors.MetricExtractor
import com.sortinghat.metrics_extractor.domain.behaviors.PerComponentResult
import com.sortinghat.metrics_extractor.domain.model.System

/**
 * Number of clients that consume messages published by a given component
 * Dimension: Async Coupling
 */
class ClientsThatConsumeMessagesPublishedMetric: MetricExtractor {
    override fun execute(system: System): ExtractionResult {
        val forModules = system.asyncOperations
            .filter { it.from.module != it.to.module }
            .groupBy { it.from.module }
            .mapValues { it.value.size }

        val forServices = system.asyncOperations
            .groupBy { it.from }
            .mapValues { it.value.size }

        return PerComponentResult(
            modules = system.modules.associateWith { forModules[it] ?: 0 },
            services = system.services.associateWith { forServices[it] ?: 0 }
        )
    }
}
