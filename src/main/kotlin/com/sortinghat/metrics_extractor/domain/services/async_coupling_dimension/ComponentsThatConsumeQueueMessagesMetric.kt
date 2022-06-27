package com.sortinghat.metrics_extractor.domain.services.async_coupling_dimension

import com.sortinghat.metrics_extractor.domain.behaviors.*
import com.sortinghat.metrics_extractor.domain.model.Service

class ComponentsThatConsumeQueueMessagesMetric(
    private val visitorBag: VisitorBag = VisitorBag()
) : MetricExtractor, Visitor by visitorBag {

    private val servicesThatConsumeMessages = hashSetOf<Service>()

    override fun getResult(): ValueResultPerComponentType {
        return ValueResultPerComponentType(
            services = servicesThatConsumeMessages.size,
            modules = servicesThatConsumeMessages.groupBy { s -> s.module }.keys.size
        )
    }

    override fun getMetricDescription(): String {
        return "Number of components that consume messages from the queue"
    }

    override fun visit(s: Service) {
        if (s in visitorBag.visited) return

        visitorBag.addVisited(s)
        if (s.channelsSubscribing.size > 0) servicesThatConsumeMessages.add(s)
        s.children().forEach { it.accept(this) }
    }
}
