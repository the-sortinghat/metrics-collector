package com.sortinghat.metrics_extractor.domain.services.size_dimension

import com.sortinghat.metrics_extractor.domain.behaviors.*
import com.sortinghat.metrics_extractor.domain.model.Operation
import com.sortinghat.metrics_extractor.domain.model.Service

class OperationsPerComponentMetric(
    private val visitorBag: VisitorBag = VisitorBag()
) : MetricExtractor, Visitor by visitorBag {

    private val operationsByService = mutableMapOf<Service, Set<Operation>>()

    override fun getResult(): PerComponentResult {
        val servicesResult = operationsByService.mapValues { it.value.size }
        val modulesResult = servicesResult.keys
            .groupBy { s -> s.module }
            .mapValues { it.value.sumOf { s -> servicesResult.getOrDefault(s, 0) } }

        return PerComponentResult(
            modules = modulesResult.mapKeys { it.key.name },
            services = servicesResult.mapKeys { it.key.name },
        )
    }

    override fun getMetricDescription(): String {
        return "Number of operations per component"
    }

    override fun visit(s: Service) {
        if (s in visitorBag.visited) return

        visitorBag.addVisited(s)
        operationsByService[s] = s.exposedOperations
        s.children().forEach { it.accept(this) }
    }
}
