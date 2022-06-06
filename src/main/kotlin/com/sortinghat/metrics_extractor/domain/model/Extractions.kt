package com.sortinghat.metrics_extractor.domain.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.sortinghat.metrics_extractor.domain.behaviors.ExtractionResult

@JsonSerialize
data class Extractions(
    @JsonProperty("Size") val size: Map<String, ExtractionResult>,
    @JsonProperty("Data source coupling") val dataCoupling: Map<String, ExtractionResult>,
    @JsonProperty("Synchronous coupling") val syncCoupling: Map<String, ExtractionResult>,
    @JsonProperty("Asynchronous coupling") val asyncCoupling: Map<String, ExtractionResult>
)
