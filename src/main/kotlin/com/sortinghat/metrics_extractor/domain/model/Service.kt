package com.sortinghat.metrics_extractor.domain.model

import com.sortinghat.metrics_extractor.domain.behaviors.Visitable
import com.sortinghat.metrics_extractor.domain.behaviors.Visitor

data class Service(
    val name: String,
    val responsibility: String,
    val module: Module,
    val system: ServiceBasedSystem,
    val databasesUsages: MutableSet<Database> = mutableSetOf(),
    val exposedOperations: MutableSet<Operation> = mutableSetOf(),
    val consumedOperations: MutableSet<Operation> = mutableSetOf(),
    val channelsPublishing: MutableSet<MessageChannel> = mutableSetOf(),
    val channelsSubscribing: MutableSet<MessageChannel> = mutableSetOf()
) : Visitable {

    fun addUsage(db: Database, accessType: DatabaseAccessType) {
        databasesUsages.add(db)

        if (this !in db.usages()) db.addUsage(this, accessType)
    }

    fun expose(op: Operation) {
        exposedOperations.add(op)
    }

    fun consume(op: Operation) {
        consumedOperations.add(op)
    }

    fun publishTo(ch: MessageChannel) {
        channelsPublishing.add(ch)
    }

    fun subscribeTo(ch: MessageChannel) {
        channelsSubscribing.add(ch)
    }

    override fun accept(v: Visitor) {
        v.visit(this)
    }

    override fun children() = emptySet<Visitable>() +
            module +
            system +
            databasesUsages +
            exposedOperations +
            consumedOperations +
            channelsPublishing +
            channelsSubscribing

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Service) return false

        if (name != other.name) return false
        if (module != other.module) return false
        if (system != other.system) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + module.hashCode()
        result = 31 * result + system.hashCode()
        return result
    }
}
