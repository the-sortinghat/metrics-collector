package com.sortinghat.metrics_extractor.domain.services.async_coupling_dimension

import com.sortinghat.metrics_extractor.domain.behaviors.ManyComponentsPerComponentResult
import com.sortinghat.metrics_extractor.domain.behaviors.MetricExtractor
import com.sortinghat.metrics_extractor.domain.behaviors.Visitor
import com.sortinghat.metrics_extractor.domain.behaviors.VisitorBag
import com.sortinghat.metrics_extractor.domain.model.MessageChannel
import com.sortinghat.metrics_extractor.domain.model.Module
import com.sortinghat.metrics_extractor.domain.model.Service

class MessagesConsumedByEachDependingComponentMetric(
    private val visitorBag: VisitorBag = VisitorBag()
) : MetricExtractor, Visitor by visitorBag {

    private val serviceToPublishedChannels = mutableMapOf<Service, Set<MessageChannel>>()
    private val channelToSubscribers = mutableMapOf<MessageChannel, Set<Service>>()

    override fun getResult(): ManyComponentsPerComponentResult {
        val servicesResult = serviceToPublishedChannels.mapValues { (_, publishedChannels) ->
            val subscriberToNumberOfConsumedMessagesMap = mutableMapOf<Service, Int>()

            publishedChannels.forEach { channel ->
                channelToSubscribers
                    .getOrDefault(channel, setOf())
                    .forEach { consumer ->
                        if (consumer !in subscriberToNumberOfConsumedMessagesMap) {
                            subscriberToNumberOfConsumedMessagesMap[consumer] = 1
                        } else {
                            subscriberToNumberOfConsumedMessagesMap.merge(consumer, 1) { old, new ->
                                old.plus(new)
                            }
                        }
                    }
            }

            subscriberToNumberOfConsumedMessagesMap
        }

        val modulesResult = serviceToPublishedChannels.keys
            .groupBy { service -> service.module }
            .mapValues { (_, services) ->
                services.fold(setOf<MessageChannel>()) { acc, service ->
                    acc.plus(serviceToPublishedChannels.getOrDefault(service, setOf()))
                }
            }
            .mapValues { (module, publishedChannels) ->
                val subscriberToNumberOfConsumedMessagesMap = mutableMapOf<Module, Int>()

                publishedChannels.forEach { channel ->
                    channelToSubscribers
                        .getOrDefault(channel, setOf())
                        .filter { consumer -> consumer.module != module }
                        .distinctBy { consumer -> consumer.module }
                        .forEach { consumer ->
                            if (consumer.module !in subscriberToNumberOfConsumedMessagesMap) {
                                subscriberToNumberOfConsumedMessagesMap[consumer.module] = 1
                            } else {
                                subscriberToNumberOfConsumedMessagesMap.merge(consumer.module, 1) { old, new ->
                                    old.plus(new)
                                }
                            }
                        }
                }

                subscriberToNumberOfConsumedMessagesMap
            }

        return ManyComponentsPerComponentResult(
            modules = modulesResult.mapKeys { it.key.name }.mapValues { it.value.mapKeys { entry -> entry.key.name } },
            services = servicesResult.mapKeys { it.key.name }.mapValues { it.value.mapKeys { entry -> entry.key.name } }
        )
    }

    override fun getMetricDescription(): String {
        return "Number of different types of messages consumed by each depending component"
    }

    override fun visit(s: Service) {
        if (s in visitorBag.visited) return

        visitorBag.addVisited(s)
        serviceToPublishedChannels[s] = s.channelsPublishing
        s.channelsSubscribing.forEach { channel ->
            channelToSubscribers.merge(channel, setOf(s)) { old, new -> old.plus(new) }
        }
        s.children().forEach { it.accept(this) }
    }
}
