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
    companion object {
        fun create(s: Service, db: Database, namespace: String, role: String, accessType: String): DatabaseUsage {
            val rawType = accessType.split("and", ",", "/").map { it.trim() }
            val type =
                if (rawType.size > 1) DatabaseAccessType.ReadWrite
                else if (listOf("write", "writing").any { rawType.first().lowercase() == it }) DatabaseAccessType.Write
                else DatabaseAccessType.Read

            return DatabaseUsage(s, db, namespace, role, type)
        }
    }

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
