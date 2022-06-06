package com.sortinghat.metrics_extractor.domain.model

import com.sortinghat.metrics_extractor.domain.behaviors.Visitable
import com.sortinghat.metrics_extractor.domain.behaviors.Visitor

data class ServiceBasedSystem(val name: String, val description: String): Visitable {

    override fun accept(v: Visitor) {
        v.visit(this)
    }

    override fun children() = emptySet<Visitable>()
}
