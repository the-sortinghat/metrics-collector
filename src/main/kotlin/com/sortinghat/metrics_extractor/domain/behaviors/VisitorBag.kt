package com.sortinghat.metrics_extractor.domain.behaviors

import com.sortinghat.metrics_extractor.domain.model.*

class VisitorBag : Visitor {

    val visited = mutableSetOf<Visitable>()

    override fun visit(s: Service) {
        if (s in visited) return

        visited.add(s)
        s.children().forEach { it.accept(this) }
    }

    override fun visit(db: Database) {
        if (db in visited) return

        visited.add(db)
        db.children().forEach { it.accept(this) }
    }

    override fun visit(op: Operation) {
        if (op in visited) return

        visited.add(op)
        op.children().forEach { it.accept(this) }
    }

    override fun visit(ch: MessageChannel) {
        if (ch in visited) return

        visited.add(ch)
        ch.children().forEach { it.accept(this) }
    }

    override fun visit(module: Module) {
        if (module in visited) return

        visited.add(module)
        module.children().forEach { it.accept(this) }
    }

    override fun visit(system: ServiceBasedSystem) {
        if (system in visited) return

        visited.add(system)
        system.children().forEach { it.accept(this) }
    }

    fun addVisited(visitable: Visitable) {
        visited.add(visitable)
    }
}
