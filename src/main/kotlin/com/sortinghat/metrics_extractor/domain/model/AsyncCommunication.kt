package com.sortinghat.metrics_extractor.domain.model

data class AsyncCommunication(val from: Service, val to: Service, val channel: MessageChannel)
