package com.sortinghat.metrics_extractor.domain.model

@Suppress("unused")
enum class DataSource {
    MySql, PostgreSql, MariaDb,
    CassandraDb, MongoDb, Redis,
    SqlServer, Oracle, Neo4j,
    PlainText, Relational, Document,
    Graph, Column, KeyValue
}

data class Database(
    val id: String,
    val model: DataSource
) {
    companion object {
        fun create(id: String, rawModel: String): Database {
            val model = DataSource.values().find { it.toString().lowercase() == rawModel.lowercase() }
                ?: throw IllegalArgumentException("database model is not valid")
            return Database(id, model)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Service) return false

        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
