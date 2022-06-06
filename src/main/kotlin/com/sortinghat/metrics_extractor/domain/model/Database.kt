package com.sortinghat.metrics_extractor.domain.model

import com.sortinghat.metrics_extractor.domain.behaviors.Visitable
import com.sortinghat.metrics_extractor.domain.behaviors.Visitor

enum class DataSource {
    MySql, Postgres, MariaDb,
    CassandraDb, MongoDb, Redis,
    SqlServer, Oracle, Neo4j,
    PlainText, Relational, Document,
    Graph, Column, KeyValue
}

@Suppress("unused")
enum class DatabaseAccessType {
    Read, Write, ReadWrite
}

data class Database(
    val namespace: String,
    val model: DataSource,
    private val usedBy: MutableMap<Service, DatabaseAccessType> = mutableMapOf()
) : Visitable {

    companion object {
        fun create(namespace: String, rawModel: String): Database {
            val model = DataSource.values().find { it.toString().lowercase() == rawModel.lowercase() }
                ?: throw IllegalArgumentException("database model is not valid")
            return Database(namespace, model)
        }
    }

    fun usages() = usedBy.keys

    fun addUsage(s: Service, accessType: DatabaseAccessType) {
        usedBy[s] = accessType

        if (this !in s.databasesUsages) s.addUsage(this, accessType)
    }

    fun getAccessType(s: Service) = usedBy[s]

    override fun accept(v: Visitor) {
        v.visit(this)
    }

    override fun children() = usages()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Database) return false

        if (namespace != other.namespace) return false
        if (model != other.model) return false
        return true
    }

    override fun hashCode(): Int {
        var result = namespace.hashCode()
        result = 31 * result + model.hashCode()
        return result
    }
}
