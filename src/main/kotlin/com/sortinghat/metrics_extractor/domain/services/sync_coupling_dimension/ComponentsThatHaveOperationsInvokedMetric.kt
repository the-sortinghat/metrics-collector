package com.sortinghat.metrics_extractor.domain.services.sync_coupling_dimension

import com.sortinghat.metrics_extractor.domain.behaviors.*
import com.sortinghat.metrics_extractor.domain.model.Module
import com.sortinghat.metrics_extractor.domain.model.Operation
import com.sortinghat.metrics_extractor.domain.model.Service

class ComponentsThatHaveOperationsInvokedMetric(
    private val visitorBag: VisitorBag = VisitorBag()
) : MetricExtractor, Visitor by visitorBag {

    private val serviceToConsumedOperations = mutableMapOf<Service, Set<Operation>>()
    private val operationToExposers = mutableMapOf<Operation, Set<Service>>()

    override fun getResult(): PerComponentResult {
        val servicesResult = mutableMapOf<Service, Int>()
        val modulesResult = mutableMapOf<Module, Int>()

        serviceToConsumedOperations.forEach { (service, operations) ->
            servicesResult[service] = operations
                .fold(setOf<Service>()) { acc, operation ->
                    acc.plus(
                        operationToExposers.getOrDefault(
                            operation,
                            setOf()
                        )
                    )
                }
                .size
        }

        serviceToConsumedOperations.keys
            .groupBy { it.module }
            .mapValues {
                it.value.fold(setOf<Operation>()) { acc, service ->
                    acc.plus(
                        serviceToConsumedOperations.getOrDefault(
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
                            operationToExposers.getOrDefault(
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
        return "Number of components from which a given component invokes operations"
    }

    override fun visit(s: Service) {
        if (s in visitorBag.visited) return

        visitorBag.addVisited(s)
        serviceToConsumedOperations[s] = s.consumedOperations
        s.exposedOperations.forEach { operation ->
            operationToExposers.merge(operation, setOf(s)) { old, new -> old.plus(new) }
        }
        s.children().forEach { it.accept(this) }
    }
}
