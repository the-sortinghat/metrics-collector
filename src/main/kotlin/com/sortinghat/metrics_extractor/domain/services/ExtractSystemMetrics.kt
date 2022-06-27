package com.sortinghat.metrics_extractor.domain.services

import com.sortinghat.metrics_extractor.domain.model.DatabaseAccessType
import com.sortinghat.metrics_extractor.domain.model.Extractions
import com.sortinghat.metrics_extractor.domain.model.ServiceRepository
import com.sortinghat.metrics_extractor.domain.services.async_coupling_dimension.ClientsThatConsumeMessagesPublishedMetric
import com.sortinghat.metrics_extractor.domain.services.async_coupling_dimension.ComponentsThatHaveMessagesConsumedMetric
import com.sortinghat.metrics_extractor.domain.services.data_coupling_dimension.DataSourcesPerComponentMetric
import com.sortinghat.metrics_extractor.domain.services.data_coupling_dimension.DatabasesByAccessTypeMetric
import com.sortinghat.metrics_extractor.domain.services.data_coupling_dimension.SharedDatabasesMetric
import com.sortinghat.metrics_extractor.domain.services.size_dimension.*
import com.sortinghat.metrics_extractor.domain.services.sync_coupling_dimension.ClientsThatInvokeOperationsMetric
import com.sortinghat.metrics_extractor.domain.services.sync_coupling_dimension.ComponentsThatHaveOperationsInvokedMetric
import com.sortinghat.metrics_extractor.domain.services.sync_coupling_dimension.OperationsInvokedMetric

class ExtractSystemMetrics(private val repository: ServiceRepository) {

    fun execute(systemName: String): Extractions {
        val services = repository.findAllBySystem(systemName)
        val sizeMetricsExtractors = listOf(
            SystemComponentsMetric(),
            DeploymentDependencyMetric(),
            OperationsPerComponentMetric(),
            LargestServiceMetric(),
            SmallestServiceMetric()
        )
        val dataCouplingMetricsExtractors = listOf(
            SharedDatabasesMetric(),
            DataSourcesPerComponentMetric(),
            DatabasesByAccessTypeMetric(DatabaseAccessType.Read),
            DatabasesByAccessTypeMetric(DatabaseAccessType.Write),
            DatabasesByAccessTypeMetric(DatabaseAccessType.ReadWrite)
        )
        val syncCouplingMetricsExtractors = listOf(
            ClientsThatInvokeOperationsMetric(),
            ComponentsThatHaveOperationsInvokedMetric(),
            OperationsInvokedMetric()
        )
        val asyncCouplingMetricsExtractors = listOf(
            ClientsThatConsumeMessagesPublishedMetric(),
            ComponentsThatHaveMessagesConsumedMetric()
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
