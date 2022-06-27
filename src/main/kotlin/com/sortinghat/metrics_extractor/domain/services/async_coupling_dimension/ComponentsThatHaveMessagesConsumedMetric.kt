package com.sortinghat.metrics_extractor.domain.services.async_coupling_dimension

import com.sortinghat.metrics_extractor.domain.behaviors.MetricExtractor
import com.sortinghat.metrics_extractor.domain.behaviors.PerComponentResult
import com.sortinghat.metrics_extractor.domain.behaviors.Visitor
import com.sortinghat.metrics_extractor.domain.behaviors.VisitorBag
import com.sortinghat.metrics_extractor.domain.model.MessageChannel
import com.sortinghat.metrics_extractor.domain.model.Module
import com.sortinghat.metrics_extractor.domain.model.Service

class ComponentsThatHaveMessagesConsumedMetric(
    private val visitorBag: VisitorBag = VisitorBag()
) : MetricExtractor, Visitor by visitorBag {

    private val serviceToSubscribedMessages = mutableMapOf<Service, Set<MessageChannel>>()
    private val messageToPublishers = mutableMapOf<MessageChannel, Set<Service>>()

    override fun getResult(): PerComponentResult {
        val servicesResult = mutableMapOf<Service, Int>()
        val modulesResult = mutableMapOf<Module, Int>()

        serviceToSubscribedMessages.forEach { (service, channels) ->
            servicesResult[service] = channels
                .fold(setOf<Service>()) { acc, channel ->
                    acc.plus(
                        messageToPublishers.getOrDefault(
                            channel,
                            setOf()
                        )
                    )
                }
                .size
        }

        serviceToSubscribedMessages.keys
            .groupBy { it.module }
            .mapValues {
                it.value.fold(setOf<MessageChannel>()) { acc, service ->
                    acc.plus(
                        serviceToSubscribedMessages.getOrDefault(
                            service,
                            setOf()
                        )
                    )
                }
            }
            .forEach { (module, channels) ->
                modulesResult[module] = channels
                    .fold(setOf<Service>()) { acc, channel ->
                        acc.plus(
                            messageToPublishers.getOrDefault(
                                channel,
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
        return "Number of components from which a given component consumes messages"
    }

    override fun visit(s: Service) {
        if (s in visitorBag.visited) return

        visitorBag.addVisited(s)
        serviceToSubscribedMessages[s] = s.channelsSubscribing
        s.channelsPublishing.forEach { channel ->
            messageToPublishers.merge(channel, setOf(s)) { old, new -> old.plus(new) }
        }
        s.children().forEach { it.accept(this) }
    }
}
