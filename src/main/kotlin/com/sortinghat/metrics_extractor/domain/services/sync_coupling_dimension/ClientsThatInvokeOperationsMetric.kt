package com.sortinghat.metrics_extractor.domain.services.sync_coupling_dimension

import com.sortinghat.metrics_extractor.domain.behaviors.ExtractionResult
import com.sortinghat.metrics_extractor.domain.behaviors.MetricExtractor
import com.sortinghat.metrics_extractor.domain.behaviors.PerComponentResult
import com.sortinghat.metrics_extractor.domain.model.System

/**
 * Number of clients that invoke the operations of a given component
 * Dimension: Sync Coupling
 */
class ClientsThatInvokeOperationsMetric: MetricExtractor {
    override fun execute(system: System): ExtractionResult {
        val forModules = system.syncOperations
            .filter { it.from.module != it.to.module }
            .groupBy { it.to.module }
            .mapValues { it.value.distinctBy { sync -> sync.from } }
            .mapValues { it.value.size }

        val forServices = system.syncOperations
            .groupBy { it.to }
            .mapValues { it.value.distinctBy { sync -> sync.from } }
            .mapValues { it.value.size }

        return PerComponentResult(
            modules = system.modules.associateWith { forModules[it] ?: 0 },
            services = system.services.associateWith { forServices[it] ?: 0 }
        )
    }
}
