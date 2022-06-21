package com.sortinghat.metrics_extractor.domain.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.sortinghat.metrics_extractor.domain.behaviors.ExtractionResult
import com.sortinghat.metrics_extractor.domain.behaviors.ValueResult

@Suppress("UNCHECKED_CAST")
@JsonSerialize
class Extractions(
    size: Map<String, ExtractionResult>,
    dataCoupling: Map<String, ExtractionResult>,
    syncCoupling: Map<String, ExtractionResult>,
    asyncCoupling: Map<String, ExtractionResult>
) {
    @JsonProperty("Size")
    val size: Map<String, ExtractionResult>
        get() = mapValues(field) as Map<String, ExtractionResult>

    @JsonProperty("Data source coupling")
    val dataCoupling: Map<String, ExtractionResult>
        get() = mapValues(field) as Map<String, ExtractionResult>

    @JsonProperty("Synchronous coupling")
    val syncCoupling: Map<String, ExtractionResult>
        get() = mapValues(field) as Map<String, ExtractionResult>

    @JsonProperty("Asynchronous coupling")
    val asyncCoupling: Map<String, ExtractionResult>
        get() = mapValues(field) as Map<String, ExtractionResult>

    init {
        this.size = size
        this.dataCoupling = dataCoupling
        this.syncCoupling = syncCoupling
        this.asyncCoupling = asyncCoupling
    }

    private fun mapValues(map: Map<String, ExtractionResult>) =
        map.mapValues {
            if (it.value is ValueResult) {
                (it.value as ValueResult).value
            } else {
                it.value
            }
        }
}
