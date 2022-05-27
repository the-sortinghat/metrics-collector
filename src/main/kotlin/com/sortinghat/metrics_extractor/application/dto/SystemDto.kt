package com.sortinghat.metrics_extractor.application.dto

import com.sortinghat.metrics_extractor.domain.model.*

data class SystemDto(
    val id: String,
    val name: String,
    val description: String,
    val modules: List<ModuleDto>,
    val services: List<ServiceDto>,
    val databases: List<DatabaseDto>,
    val databasesUsages: List<DatabaseUsageDto>,
    val syncOperations: List<OperationDto>,
    val asyncOperations: List<OperationDto>
) {
    companion object {
        fun create(system: System) = SystemDto(
            id = system.id,
            name = system.name,
            description = system.description,
            modules = system.modules.map { ModuleDto.create(it) },
            services = system.services.map { ServiceDto.create(it) },
            databases = system.databases.map { DatabaseDto.create(it) },
            databasesUsages = system.usages.map { DatabaseUsageDto.create(it) },
            syncOperations = system.syncOperations.map { OperationDto.createSynchronous(it) },
            asyncOperations = system.asyncOperations.map { OperationDto.createAsynchronous(it) },
        )
    }
}

data class ModuleDto(
    val id: String,
    val name: String
) {
    companion object {
        fun create(module: Module) = ModuleDto(
            id = module.id,
            name = module.name
        )
    }
}

data class ServiceDto(
    val id: String,
    val name: String,
    val responsibility: String,
    val operations: List<String>,
    val moduleId: String
) {
    companion object {
        fun create(service: Service) = ServiceDto(
            id = service.id,
            name = service.name,
            responsibility = service.responsibility,
            operations = service.operations.map { it.toString() },
            moduleId = service.module.id
        )
    }
}

data class DatabaseDto(
    val id: String,
    val model: String
) {
    companion object {
        fun create(database: Database) = DatabaseDto(
            id = database.id,
            model = database.model.toString()
        )
    }
}

data class DatabaseUsageDto(
    val databaseId: String,
    val serviceId: String,
    val role: String,
    val namespace: String,
    val accessType: String
) {
    companion object {
        fun create(usage: DatabaseUsage) = DatabaseUsageDto(
            serviceId = usage.service.id,
            databaseId = usage.database.id,
            role = usage.role,
            namespace = usage.namespace,
            accessType = usage.accessType.toString()
        )
    }
}

data class OperationDto(
    val from: String,
    val to: String,
    val label: String
) {
    companion object {
        fun createSynchronous(syncCommunication: SyncCommunication) = OperationDto(
            from = syncCommunication.from.id,
            to = syncCommunication.to.id,
            label = syncCommunication.operation.toString()
        )

        fun createAsynchronous(asyncCommunication: AsyncCommunication) = OperationDto(
            from = asyncCommunication.from.id,
            to = asyncCommunication.to.id,
            label = asyncCommunication.channel.name
        )
    }
}
