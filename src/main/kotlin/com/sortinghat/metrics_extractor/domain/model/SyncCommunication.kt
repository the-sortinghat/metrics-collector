package com.sortinghat.metrics_collector.domain.model

data class SyncCommunication(val from: Service, val to: Service, val operation: Operation)
