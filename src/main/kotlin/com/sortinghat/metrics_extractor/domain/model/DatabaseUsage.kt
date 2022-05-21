package com.sortinghat.metrics_extractor.domain.model

@Suppress("unused")
enum class DatabaseAccessType {
    Read, Write, ReadWrite
}

data class DatabaseUsage(
    val service: Service,
    val database: Database,
    val namespace: String,
    val role: String,
    val accessType: DatabaseAccessType
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DatabaseUsage) return false

        if (service != other.service) return false
        if (database != other.database) return false
        if (namespace != other.namespace) return false

        return true
    }

    override fun hashCode(): Int {
        var result = service.hashCode()
        result = 31 * result + database.hashCode()
        result = 31 * result + namespace.hashCode()
        return result
    }
}
