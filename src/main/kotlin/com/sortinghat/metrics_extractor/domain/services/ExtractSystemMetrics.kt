package com.sortinghat.metrics_extractor.domain.services

import com.sortinghat.metrics_extractor.domain.model.Extractions
import com.sortinghat.metrics_extractor.domain.model.ServiceRepository
import com.sortinghat.metrics_extractor.domain.services.async_coupling_dimension.ClientsThatConsumeMessagesPublishedMetric
import com.sortinghat.metrics_extractor.domain.services.data_coupling_dimension.DataSourcesPerComponentMetric
import com.sortinghat.metrics_extractor.domain.services.data_coupling_dimension.SharedDatabasesMetric
import com.sortinghat.metrics_extractor.domain.services.size_dimension.DeploymentDependencyMetric
import com.sortinghat.metrics_extractor.domain.services.sync_coupling_dimension.ClientsThatInvokeOperationsMetric

class ExtractSystemMetrics(private val repository: ServiceRepository) {

    fun execute(systemName: String): Extractions {
        val services = repository.findAllBySystem(systemName)
        val sizeMetricsExtractors = listOf(
            DeploymentDependencyMetric()
        )
        val dataCouplingMetricsExtractors = listOf(
            SharedDatabasesMetric(),
            DataSourcesPerComponentMetric()
        )
        val syncCouplingMetricsExtractors = listOf(
            ClientsThatInvokeOperationsMetric()
        )
        val asyncCouplingMetricsExtractors = listOf(
            ClientsThatConsumeMessagesPublishedMetric()
        )

        sizeMetricsExtractors.forEach { extractor ->
            services.forEach { service -> service.accept(extractor) }
        }
        dataCouplingMetricsExtractors.forEach { extractor ->
            services.forEach { service -> service.accept(extractor) }
        }
        syncCouplingMetricsExtractors.forEach { extractor ->
            services.forEach { service -> service.accept(extractor) }
        }
        asyncCouplingMetricsExtractors.forEach { extractor ->
            services.forEach { service -> service.accept(extractor) }
        }

        return Extractions(
            size = sizeMetricsExtractors.fold(mapOf()) { acc, extractor ->
                acc.plus(extractor.getMetricDescription() to extractor.getResult())
            },
            dataCoupling = dataCouplingMetricsExtractors.fold(mapOf()) { acc, extractor ->
                acc.plus(extractor.getMetricDescription() to extractor.getResult())
            },
            syncCoupling = syncCouplingMetricsExtractors.fold(mapOf()) { acc, extractor ->
                acc.plus(extractor.getMetricDescription() to extractor.getResult())
            },
            asyncCoupling = asyncCouplingMetricsExtractors.fold(mapOf()) { acc, extractor ->
                acc.plus(extractor.getMetricDescription() to extractor.getResult())
            },
        )
    }
}
