package com.sortinghat.metrics_extractor.domain.model

enum class HttpVerb {
    GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD, CONNECT, TRACE
}

data class Operation(val verb: HttpVerb, val uri: String) {
    companion object {
        fun fromString(url: String): Operation {
            try {
                val (rawVerb, uri) = url.trim().split(" ")
                val httpVerb = HttpVerb.values().find { it.toString() == rawVerb }
                    ?: throw IllegalArgumentException("http verb must be valid")
                return Operation(httpVerb, uri)
            } catch (ex: Exception) {
                throw IllegalArgumentException("operation must be in the format VERB + URI with a single space")
            }
        }
    }
}
