package com.sortinghat.metrics_extractor.domain.services.sync_coupling_dimension

import com.sortinghat.metrics_extractor.domain.behaviors.MetricExtractor
import com.sortinghat.metrics_extractor.domain.behaviors.PerComponentResult
import com.sortinghat.metrics_extractor.domain.behaviors.Visitor
import com.sortinghat.metrics_extractor.domain.model.Module
import com.sortinghat.metrics_extractor.domain.model.Operation
import com.sortinghat.metrics_extractor.domain.model.Service
import com.sortinghat.metrics_extractor.domain.behaviors.VisitorBag

class ClientsThatInvokeOperationsMetric(
    private val visitorBag: VisitorBag = VisitorBag()
) : MetricExtractor, Visitor by visitorBag {

    private val serviceToOperations = mutableMapOf<Service, Set<Operation>>()
    private val operationToServices = mutableMapOf<Operation, Set<Service>>()

    override fun getResult(): PerComponentResult {
        val servicesResult = mutableMapOf<Service, Int>()
        val modulesResult = mutableMapOf<Module, Int>()

        serviceToOperations.forEach { (service, operations) ->
            servicesResult[service] = operations
                .fold(setOf<Service>()) { acc, operation ->
                    acc.plus(
                        operationToServices.getOrDefault(
                            operation,
                            setOf()
                        )
                    )
                }
                .size
        }

        serviceToOperations.keys
            .groupBy { it.module }
            .mapValues {
                it.value.fold(setOf<Operation>()) { acc, service ->
                    acc.plus(
                        serviceToOperations.getOrDefault(
                            service,
                            setOf()
                        )
                    )
                }
            }
            .forEach { (module, operations) ->
                modulesResult[module] = operations
                    .fold(setOf<Service>()) { acc, operation ->
                        acc.plus(
                            operationToServices.getOrDefault(
                                operation,
                                setOf()
                            )
                        )
                    }
                    .filter { it.module != module }
                    .distinctBy { it.module }
                    .size
            }

        return PerComponentResult(
            modules = modulesResult.mapKeys { it.key.name },
            services = servicesResult.mapKeys { it.key.name }
        )
    }

    override fun getMetricDescription(): String {
        return "Number of clients that invoke the operations of a given component"
    }

    override fun visit(s: Service) {
        if (s in visitorBag.visited) return

        visitorBag.addVisited(s)
        serviceToOperations[s] = s.exposedOperations
        s.consumedOperations.forEach { operationToServices.merge(it, setOf(s)) { old, new -> old.plus(new) } }
        s.children().forEach { it.accept(this) }
    }
}
