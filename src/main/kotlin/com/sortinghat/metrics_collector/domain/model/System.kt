package com.sortinghat.metrics_collector.domain.model

data class System(
    val id: String,
    val name: String,
    val description: String
) {
    val modules: MutableSet<Module> = mutableSetOf()
    val services: MutableSet<Service> = mutableSetOf()
    val databases: MutableSet<Database> = mutableSetOf()
    val usages: MutableSet<DatabaseUsage> = mutableSetOf()
    val syncOperations: MutableSet<SyncCommunication> = mutableSetOf()
    val asyncOperations: MutableSet<AsyncCommunication> = mutableSetOf()

    fun addModule(module: Module) {
        modules.add(module)
        module.services.forEach { services.add(it) }
    }

    fun addService(s: Service) {
        services.add(s)
        modules.add(s.module)
    }

    fun addDatabase(database: Database) {
        databases.add(database)
    }

    fun addUsage(usage: DatabaseUsage) {
        addService(usage.service)
        addDatabase(usage.database)
        usages.add(usage)
    }

    fun addSyncOperation(sync: SyncCommunication) {
        addService(sync.from)
        addService(sync.to)
        syncOperations.add(sync)
    }

    fun addAsyncOperation(async: AsyncCommunication) {
        addService(async.from)
        addService(async.to)
        asyncOperations.add(async)
    }
}
