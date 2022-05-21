package com.sortinghat.metrics_extractor.domain.model

data class Module(
    val id: String,
    val name: String,
    val services: MutableSet<Service> = mutableSetOf()
) {
    fun addService(s: Service) {
        services.add(s)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Module) return false

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
