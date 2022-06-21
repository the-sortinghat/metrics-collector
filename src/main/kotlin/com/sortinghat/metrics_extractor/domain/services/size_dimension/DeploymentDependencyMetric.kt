package com.sortinghat.metrics_extractor.domain.services.size_dimension

import com.sortinghat.metrics_extractor.domain.behaviors.*
import com.sortinghat.metrics_extractor.domain.model.Module
import com.sortinghat.metrics_extractor.domain.model.Service

class DeploymentDependencyMetric(
    private val visitorBag: VisitorBag = VisitorBag()
) : MetricExtractor, Visitor by visitorBag {

    private val moduleToServices = mutableMapOf<Module, Set<Service>>()

    override fun getResult(): ValueResult {
        return ValueResult(
            value = moduleToServices
                .filterValues { it.size > 1 }
                .values
                .fold(0) { sum, services -> sum + services.size }
        )
    }

    override fun visit(s: Service) {
        if (s in visitorBag.visited) return

        visitorBag.addVisited(s)
        moduleToServices.merge(s.module, setOf(s)) { old, new -> old.plus(new) }
        s.children().forEach { it.accept(this) }
    }

    override fun getMetricDescription(): String {
        return "Number of services with deployment dependency"
    }
}
