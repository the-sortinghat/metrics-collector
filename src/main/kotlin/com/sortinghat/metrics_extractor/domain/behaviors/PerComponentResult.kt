package com.sortinghat.metrics_extractor.domain.behaviors

data class PerComponentResult(val modules: Map<String, Int>, val services: Map<String, Int>) : ExtractionResult
