package com.sortinghat.metrics_extractor.domain.behaviors

data class ManyComponentsPerComponentResult(
    val modules: Map<String, Map<String, Int>>,
    val services: Map<String, Map<String, Int>>
) : ExtractionResult
