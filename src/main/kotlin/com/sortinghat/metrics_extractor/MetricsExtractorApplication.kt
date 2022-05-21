package com.sortinghat.metrics_extractor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MetricsExtractorApplication

fun main(args: Array<String>) {
	runApplication<MetricsExtractorApplication>(*args)
}
