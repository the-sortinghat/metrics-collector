package com.sortinghat.metrics_extractor.domain.model

data class SyncCommunication(val from: Service, val to: Service, val operation: Operation)
