package com.sortinghat.metrics_collector.domain.model

data class AsyncCommunication(val from: Service, val to: Service, val channel: MessageChannel)
