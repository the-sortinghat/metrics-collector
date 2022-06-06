package com.sortinghat.metrics_extractor.domain.behaviors

import com.sortinghat.metrics_extractor.domain.model.*

interface Visitor {
    fun visit(s: Service)
    fun visit(db: Database)
    fun visit(op: Operation)
    fun visit(ch: MessageChannel)
    fun visit(module: Module)
    fun visit(system: ServiceBasedSystem)
}
