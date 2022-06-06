package com.sortinghat.metrics_extractor.application.dto

import com.sortinghat.metrics_extractor.domain.model.*

data class SystemDto(
    val id: String,
    val name: String,
    val description: String,
    val modules: Set<ModuleDto>,
    val services: Set<ServiceDto>,
    val databases: Set<DatabaseDto>,
    val databasesUsages: Set<DatabaseUsageDto>,
    val syncOperations: Set<OperationDto>,
    val asyncOperations: Set<OperationDto>
) {
    companion object {
        fun createFromSystems(systems: Set<ServiceBasedSystem>) =
            systems.map { system ->
                SystemDto(
                    id = system.name,
                    name = system.name,
                    description = system.description,
                    modules = setOf(),
                    services = setOf(),
                    databases = setOf(),
                    databasesUsages = setOf(),
                    syncOperations = setOf(),
                    asyncOperations = setOf()
                )
            }

        fun createFromServices(services: Set<Service>): SystemDto {
            val system = services.first().system
            val modules = services.map { service -> service.module }.toSet()
            val databases = services.fold(setOf<Database>()) { acc, service -> acc.plus(service.databasesUsages) }

            return SystemDto(
                id = system.name,
                name = system.name,
                description = system.description,
                modules = ModuleDto.createMany(modules),
                services = ServiceDto.createMany(services),
                databases = DatabaseDto.createMany(databases),
                databasesUsages = DatabaseUsageDto.createMany(databases),
                syncOperations = OperationDto.createManySynchronous(services),
                asyncOperations = OperationDto.createManyAsynchronous(services),
            )
        }
    }
}

data class ModuleDto(
    val id: String,
    val name: String
) {
    companion object {
        fun createMany(modules: Set<Module>) =
            modules
                .map { module ->
                    ModuleDto(id = module.name, name = module.name)
                }
                .toSet()
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
        fun createMany(services: Set<Service>) =
            services
                .map { service ->
                    ServiceDto(
                        id = service.name,
                        name = service.name,
                        responsibility = service.responsibility,
                        operations = service.exposedOperations.map { op -> op.toString() },
                        moduleId = service.module.name
                    )
                }
                .toSet()
    }
}

data class DatabaseDto(
    val id: String,
    val model: String
) {
    companion object {
        fun createMany(databases: Set<Database>) =
            databases
                .map { db ->
                    DatabaseDto(id = db.namespace, model = db.model.toString())
                }
                .toSet()
    }
}

data class DatabaseUsageDto(
    val databaseId: String,
    val serviceId: String,
    val namespace: String,
    val accessType: String
) {
    companion object {
        fun createMany(databases: Set<Database>) = databases.fold(setOf<DatabaseUsageDto>()) { acc, database ->
            acc.plus(
                database.usages()
                    .map { service ->
                        DatabaseUsageDto(
                            serviceId = service.name,
                            databaseId = database.namespace,
                            namespace = database.namespace,
                            accessType = database.getAccessType(service)!!.toString()
                        )
                    }
                    .toSet()
            )
        }
    }
}

data class OperationDto(
    val from: String,
    val to: String,
    val label: String
) {
    companion object {
        fun createManySynchronous(services: Set<Service>): Set<OperationDto> {
            val serviceToOperations = mutableMapOf<Service, Set<Operation>>()
            val operationToServices = mutableMapOf<Operation, Set<Service>>()
            val syncOperations = mutableSetOf<OperationDto>()

            services.forEach { service ->
                serviceToOperations[service] = service.exposedOperations
                service.consumedOperations.forEach { operation ->
                    operationToServices.merge(operation, setOf(service)) { old, new -> old.plus(new) }
                }
            }

            serviceToOperations.forEach { (service, operations) ->
                operations.forEach { operation ->
                    operationToServices.getOrDefault(operation, setOf()).forEach { s ->
                        syncOperations.add(
                            OperationDto(
                                from = s.name,
                                to = service.name,
                                label = operation.toString()
                            )
                        )
                    }
                }
            }

            return syncOperations
        }

        fun createManyAsynchronous(services: Set<Service>): Set<OperationDto> {
            val serviceToChannels = mutableMapOf<Service, Set<MessageChannel>>()
            val channelToServices = mutableMapOf<MessageChannel, Set<Service>>()
            val asyncOperations = mutableSetOf<OperationDto>()

            services.forEach { service ->
                serviceToChannels[service] = service.channelsPublishing
                service.channelsSubscribing.forEach { channel ->
                    channelToServices.merge(channel, setOf(service)) { old, new -> old.plus(new) }
                }
            }

            serviceToChannels.forEach { (service, channels) ->
                channels.forEach { channel ->
                    channelToServices.getOrDefault(channel, setOf()).forEach { s ->
                        asyncOperations.add(
                            OperationDto(
                                from = service.name,
                                to = s.name,
                                label = channel.toString()
                            )
                        )
                    }
                }
            }

            return asyncOperations
        }
    }
}
