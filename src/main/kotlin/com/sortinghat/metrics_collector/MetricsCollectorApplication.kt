package com.sortinghat.metrics_collector

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MetricsCollectorApplication

fun main(args: Array<String>) {
	runApplication<MetricsCollectorApplication>(*args)
}
