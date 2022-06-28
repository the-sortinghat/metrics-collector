package com.sortinghat.metrics_extractor.domain.services.sync_coupling_dimension

import com.sortinghat.metrics_extractor.domain.behaviors.*
import com.sortinghat.metrics_extractor.domain.model.Module
import com.sortinghat.metrics_extractor.domain.model.Operation
import com.sortinghat.metrics_extractor.domain.model.Service

class OperationsInvokedByEachDependingComponentMetric(
    private val visitorBag: VisitorBag = VisitorBag()
) : MetricExtractor, Visitor by visitorBag {

    private val serviceToExposedOperations = mutableMapOf<Service, Set<Operation>>()
    private val operationToConsumers = mutableMapOf<Operation, Set<Service>>()

    override fun getResult(): ManyComponentsPerComponentResult {
        val servicesResult = serviceToExposedOperations.mapValues { (_, exposedOperations) ->
            val consumerToNumberOfConsumedOperationsMap = mutableMapOf<Service, Int>()

            exposedOperations.forEach { operation ->
                operationToConsumers
                    .getOrDefault(operation, setOf())
                    .forEach { consumer ->
                        if (consumer !in consumerToNumberOfConsumedOperationsMap) {
                            consumerToNumberOfConsumedOperationsMap[consumer] = 1
                        } else {
                            consumerToNumberOfConsumedOperationsMap.merge(consumer, 1) { old, new ->
                                old.plus(new)
                            }
                        }
                    }
            }

            consumerToNumberOfConsumedOperationsMap
        }

        val modulesResult = serviceToExposedOperations.keys
            .groupBy { service -> service.module }
            .mapValues { (_, services) ->
                services.fold(setOf<Operation>()) { acc, service ->
                    acc.plus(serviceToExposedOperations.getOrDefault(service, setOf()))
                }
            }
            .mapValues { (module, exposedOperations) ->
                val consumerToNumberOfConsumedOperationsMap = mutableMapOf<Module, Int>()

                exposedOperations.forEach { operation ->
                    operationToConsumers
                        .getOrDefault(operation, setOf())
                        .filter { consumer -> consumer.module != module }
                        .distinctBy { consumer -> consumer.module }
                        .forEach { consumer ->
                            if (consumer.module !in consumerToNumberOfConsumedOperationsMap) {
                                consumerToNumberOfConsumedOperationsMap[consumer.module] = 1
                            } else {
                                consumerToNumberOfConsumedOperationsMap.merge(consumer.module, 1) { old, new ->
                                    old.plus(new)
                                }
                            }
                        }
                }

                consumerToNumberOfConsumedOperationsMap
            }

        return ManyComponentsPerComponentResult(
            modules = modulesResult.mapKeys { it.key.name }.mapValues { it.value.mapKeys { entry -> entry.key.name } },
            services = servicesResult.mapKeys { it.key.name }.mapValues { it.value.mapKeys { entry -> entry.key.name } }
        )
    }

    override fun getMetricDescription(): String {
        return "Number of different operations invoked by each depending component"
    }

    override fun visit(s: Service) {
        if (s in visitorBag.visited) return

        visitorBag.addVisited(s)
        serviceToExposedOperations[s] = s.exposedOperations
        s.consumedOperations.forEach { operation ->
            operationToConsumers.merge(operation, setOf(s)) { old, new -> old.plus(new) }
        }
        s.children().forEach { it.accept(this) }
    }
}
