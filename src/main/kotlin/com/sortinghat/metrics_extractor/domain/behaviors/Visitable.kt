package com.sortinghat.metrics_extractor.domain.behaviors

interface Visitable {
    fun accept(v: Visitor)

    fun children(): Iterable<Visitable>
}
