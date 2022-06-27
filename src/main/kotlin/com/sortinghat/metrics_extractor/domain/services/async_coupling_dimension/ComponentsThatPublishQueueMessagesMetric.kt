package com.sortinghat.metrics_extractor.domain.services.async_coupling_dimension

import com.sortinghat.metrics_extractor.domain.behaviors.MetricExtractor
import com.sortinghat.metrics_extractor.domain.behaviors.ValueResultPerComponentType
import com.sortinghat.metrics_extractor.domain.behaviors.Visitor
import com.sortinghat.metrics_extractor.domain.behaviors.VisitorBag
import com.sortinghat.metrics_extractor.domain.model.Service

class ComponentsThatPublishQueueMessagesMetric(
    private val visitorBag: VisitorBag = VisitorBag()
) : MetricExtractor, Visitor by visitorBag {

    private val servicesThatPublishMessages = hashSetOf<Service>()

    override fun getResult(): ValueResultPerComponentType {
        return ValueResultPerComponentType(
            services = servicesThatPublishMessages.size,
            modules = servicesThatPublishMessages.groupBy { s -> s.module }.keys.size
        )
    }

    override fun getMetricDescription(): String {
        return "Number of components that publish messages in the queue"
    }

    override fun visit(s: Service) {
        if (s in visitorBag.visited) return

        visitorBag.addVisited(s)
        if (s.channelsPublishing.size > 0) servicesThatPublishMessages.add(s)
        s.children().forEach { it.accept(this) }
    }
}
