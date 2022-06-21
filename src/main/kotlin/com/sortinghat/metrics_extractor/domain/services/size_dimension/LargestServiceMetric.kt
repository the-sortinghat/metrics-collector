package com.sortinghat.metrics_extractor.domain.services.size_dimension

import com.sortinghat.metrics_extractor.domain.behaviors.MetricExtractor
import com.sortinghat.metrics_extractor.domain.behaviors.ValueResult
import com.sortinghat.metrics_extractor.domain.behaviors.Visitor
import com.sortinghat.metrics_extractor.domain.behaviors.VisitorBag
import com.sortinghat.metrics_extractor.domain.model.Service

class LargestServiceMetric(
    private val visitorBag: VisitorBag = VisitorBag()
) : MetricExtractor, Visitor by visitorBag {

    private val services = mutableSetOf<Service>()
    private var maxNumberOfOperations = 0

    override fun getResult(): ValueResult {
        val largestServices = services
            .fold(setOf<Service>()) { set, service ->
                val numberOfOperations = service.exposedOperations.size
                if (numberOfOperations == maxNumberOfOperations) {
                    set.plus(service)
                } else {
                    set
                }
            }
            .sortedBy { service -> service.name }
            .joinToString(", ") { service -> service.name }
            .plus(
                if (maxNumberOfOperations == 1)
                    " ($maxNumberOfOperations operation)"
                else
                    " ($maxNumberOfOperations operations)"
            )

        return ValueResult(value = largestServices)
    }

    override fun getMetricDescription(): String {
        return "Largest service"
    }

    override fun visit(s: Service) {
        if (s in visitorBag.visited) return

        val numberOfOperations = s.exposedOperations.size
        if (numberOfOperations > maxNumberOfOperations) {
            maxNumberOfOperations = numberOfOperations
        }

        services.add(s)
        visitorBag.addVisited(s)
        s.children().forEach { it.accept(this) }
    }
}
