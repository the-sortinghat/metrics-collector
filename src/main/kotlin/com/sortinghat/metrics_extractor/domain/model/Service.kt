package com.sortinghat.metrics_extractor.domain.model

data class Service(
    val id: String,
    val name: String,
    val responsibility: String,
    val operations: List<Operation>,
    val module: Module
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Service) return false

        if (id != other.id) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}
