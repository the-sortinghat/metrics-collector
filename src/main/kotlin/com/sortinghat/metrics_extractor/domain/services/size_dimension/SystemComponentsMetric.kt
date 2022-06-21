package com.sortinghat.metrics_extractor.domain.services.size_dimension

import com.sortinghat.metrics_extractor.domain.behaviors.*
import com.sortinghat.metrics_extractor.domain.model.Module
import com.sortinghat.metrics_extractor.domain.model.Service

class SystemComponentsMetric(
    private val visitorBag: VisitorBag = VisitorBag()
) : MetricExtractor, Visitor by visitorBag {

    private var numberOfModules = 0
    private var numberOfServices = 0

    override fun getResult(): ValueResult {
        val modulesString = if (numberOfModules == 1) "$numberOfModules module" else "$numberOfModules modules"
        val servicesString = if (numberOfServices == 1) "$numberOfServices service" else "$numberOfServices services"
        return ValueResult(value = "$servicesString and $modulesString")
    }

    override fun getMetricDescription(): String {
        return "Number of system components"
    }

    override fun visit(s: Service) {
        if (s in visitorBag.visited) return

        visitorBag.addVisited(s)
        numberOfServices += 1
        s.children().forEach { it.accept(this) }
    }

    override fun visit(module: Module) {
        if (module in visitorBag.visited) return

        visitorBag.addVisited(module)
        numberOfModules += 1
        module.children().forEach { it.accept(this) }
    }
}
