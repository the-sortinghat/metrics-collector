package com.sortinghat.metrics_extractor.domain.services.async_coupling_dimension

import com.sortinghat.metrics_extractor.domain.behaviors.*
import com.sortinghat.metrics_extractor.domain.model.*
import com.sortinghat.metrics_extractor.domain.behaviors.VisitorBag

class ClientsThatConsumeMessagesPublishedMetric(
    private val visitorBag: VisitorBag = VisitorBag()
) : MetricExtractor, Visitor by visitorBag {

    private val serviceToChannels = mutableMapOf<Service, Set<MessageChannel>>()
    private val channelToServices = mutableMapOf<MessageChannel, Set<Service>>()

    override fun getResult(): PerComponentResult {
        val servicesResult = mutableMapOf<Service, Int>()
        val modulesResult = mutableMapOf<Module, Int>()

        serviceToChannels.forEach { (service, channels) ->
            servicesResult[service] = channels
                .fold(setOf<Service>()) { acc, channel -> acc.plus(channelToServices.getOrDefault(channel, setOf())) }
                .size
        }

        serviceToChannels.keys
            .groupBy { it.module }
            .mapValues {
                it.value.fold(setOf<MessageChannel>()) { acc, service ->
                    acc.plus(
                        serviceToChannels.getOrDefault(
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
                            channelToServices.getOrDefault(
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
        return "Number of clients that consume messages published by a given component"
    }

    override fun visit(s: Service) {
        if (s in visitorBag.visited) return

        visitorBag.addVisited(s)

        serviceToChannels.putIfAbsent(s, setOf())
        s.channelsPublishing.forEach { serviceToChannels.merge(s, setOf(it)) { old, new -> old.plus(new) } }
        s.channelsSubscribing.forEach { channelToServices.merge(it, setOf(s)) { old, new -> old.plus(new) } }

        s.children().forEach { it.accept(this) }
    }
}
