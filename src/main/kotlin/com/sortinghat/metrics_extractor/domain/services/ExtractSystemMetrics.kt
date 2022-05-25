package com.sortinghat.metrics_extractor.domain.services

import com.sortinghat.metrics_extractor.domain.behaviors.ExtractionResult
import com.sortinghat.metrics_extractor.domain.services.async_coupling_dimension.ClientsThatConsumeMessagesPublishedMetric
import com.sortinghat.metrics_extractor.domain.services.data_coupling_dimension.DataSourcesPerComponentMetric
import com.sortinghat.metrics_extractor.domain.services.data_coupling_dimension.SharedDatabasesMetric
import com.sortinghat.metrics_extractor.domain.services.size_dimension.DeploymentDependencyMetric
import com.sortinghat.metrics_extractor.domain.services.sync_coupling_dimension.ClientsThatInvokeOperationsMetric
import com.sortinghat.metrics_extractor.domain.model.System

class ExtractSystemMetrics {
    companion object {
        fun execute(system: System): Map<String, Map<String, ExtractionResult>> {
            val sizeDimensionMetrics = listOf(
                DeploymentDependencyMetric()
            )
            val dataCouplingDimensionMetrics = listOf(
                DataSourcesPerComponentMetric(),
                SharedDatabasesMetric()
            )
            val syncCouplingDimensionMetrics = listOf(
                ClientsThatInvokeOperationsMetric()
            )
            val asyncCouplingDimensionMetrics = listOf(
                ClientsThatConsumeMessagesPublishedMetric()
            )

            val sizeDimension = mutableMapOf<String, ExtractionResult>()
            val dataCouplingDimension = mutableMapOf<String, ExtractionResult>()
            val syncCouplingDimension = mutableMapOf<String, ExtractionResult>()
            val asyncCouplingDimension = mutableMapOf<String, ExtractionResult>()

            sizeDimensionMetrics.forEach { sizeDimension[it.getMetricDescription()] = it.execute(system) }
            dataCouplingDimensionMetrics.forEach { dataCouplingDimension[it.getMetricDescription()] = it.execute(system) }
            syncCouplingDimensionMetrics.forEach { syncCouplingDimension[it.getMetricDescription()] = it.execute(system) }
            asyncCouplingDimensionMetrics.forEach { asyncCouplingDimension[it.getMetricDescription()] = it.execute(system) }

            return mapOf(
                "Size" to sizeDimension,
                "Data source coupling" to dataCouplingDimension,
                "Synchronous coupling" to syncCouplingDimension,
                "Asynchronous coupling" to asyncCouplingDimension
            )
        }
    }
}
