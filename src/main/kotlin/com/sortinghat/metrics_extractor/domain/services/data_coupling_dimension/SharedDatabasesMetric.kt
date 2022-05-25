package com.sortinghat.metrics_extractor.domain.services.data_coupling_dimension

import com.sortinghat.metrics_extractor.domain.behaviors.ExtractionResult
import com.sortinghat.metrics_extractor.domain.behaviors.MetricExtractor
import com.sortinghat.metrics_extractor.domain.behaviors.PerComponentResult
import com.sortinghat.metrics_extractor.domain.model.System

/**
 * Number of data sources that each component share with others
 * Dimension: Data Coupling
 */
class SharedDatabasesMetric: MetricExtractor {
    override fun execute(system: System): ExtractionResult {
        val modules = system.modules.associateWith { 0 }.toMutableMap()
        val services = system.services.associateWith { 0 }.toMutableMap()

        system.usages
            .groupBy { it.database to it.namespace }
            .mapValues { it.value.distinctBy { usage -> usage.service.module } }
            .values
            .forEach { sharedDatabases ->
                sharedDatabases.forEach { usage ->
                    modules[usage.service.module] = sharedDatabases.size - 1
                }
            }

        system.usages
            .groupBy { it.database to it.namespace }
            .values
            .forEach { sharedDatabases ->
                sharedDatabases.forEach { usage ->
                    services[usage.service] = sharedDatabases.size - 1
                }
            }

        return PerComponentResult(
            modules = modules,
            services = services
        )
    }

    override fun getMetricDescription(): String {
        return "Number of data sources that each component share with others"
    }
}
