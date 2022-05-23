package com.sortinghat.metrics_extractor.domain.behaviors

import com.sortinghat.metrics_extractor.domain.model.Module
import com.sortinghat.metrics_extractor.domain.model.Service

data class PerComponentResult(val modules: Map<Module, Int>, val services: Map<Service, Int>): ExtractionResult
