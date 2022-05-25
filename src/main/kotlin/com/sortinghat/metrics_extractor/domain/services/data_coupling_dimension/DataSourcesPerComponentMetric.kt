package com.sortinghat.metrics_extractor.domain.services.data_coupling_dimension

import com.sortinghat.metrics_extractor.domain.behaviors.ExtractionResult
import com.sortinghat.metrics_extractor.domain.behaviors.MetricExtractor
import com.sortinghat.metrics_extractor.domain.behaviors.PerComponentResult
import com.sortinghat.metrics_extractor.domain.model.System

/**
 * Number of data sources per component
 * Dimension: Data Coupling
 */
class DataSourcesPerComponentMetric: MetricExtractor {
    override fun execute(system: System): ExtractionResult {
        val forModules = system.usages
            .groupBy { it.service.module }
            .mapValues { it.value.distinctBy { usage -> usage.database to usage.namespace } }
            .mapValues { it.value.size }

        val forServices = system.usages
            .groupBy { it.service }
            .mapValues { it.value.size }

        return PerComponentResult(
            modules = system.modules.associateWith { forModules[it] ?: 0 },
            services = system.services.associateWith { forServices[it] ?: 0 }
        )
    }

    override fun getMetricDescription(): String {
        return "Number of data sources per component"
    }
}
