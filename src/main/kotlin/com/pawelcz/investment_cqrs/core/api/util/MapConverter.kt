package com.pawelcz.investment_cqrs.core.api.util



object MapConverter {
    fun stringToMap(string: String): Map<String, Double> {
        try {
            val givenString = string.replace("{", "").replace("}", "")
            return givenString.split(",")
                .map { it.split("=") }
                .map { it.first() to it.last().toDouble() }
                .toMap()
        } catch (e: Exception) {
            throw IllegalArgumentException("Wrong format")
        }

    }

    fun mapToString(map: Map<String, Double>) = map.toString()
}