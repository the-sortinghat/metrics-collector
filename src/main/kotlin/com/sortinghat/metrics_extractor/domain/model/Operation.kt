package com.sortinghat.metrics_extractor.domain.model

import com.sortinghat.metrics_extractor.domain.behaviors.Visitable
import com.sortinghat.metrics_extractor.domain.behaviors.Visitor

enum class HttpVerb {
    GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD, CONNECT, TRACE
}

data class Operation(val verb: HttpVerb, val uri: String) : Visitable {

    companion object {
        fun fromString(url: String): Operation {
            try {
                val (rawVerb, uri) = url.trim().split("/", limit = 2).map { it.trim() }
                val httpVerb = HttpVerb.values().find { it.toString() == rawVerb }
                    ?: throw IllegalArgumentException("http verb must be valid")
                return Operation(httpVerb, "/$uri")
            } catch (ex: Exception) {
                throw IllegalArgumentException("operation must be in the format VERB + URI")
            }
        }
    }

    override fun accept(v: Visitor) {
        v.visit(this)
    }

    override fun children() = emptySet<Visitable>()

    override fun toString() = "$verb $uri"
}
