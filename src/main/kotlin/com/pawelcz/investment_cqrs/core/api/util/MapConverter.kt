package com.pawelcz.investment_cqrs.core.api.util



object MapConverter {
    fun stringToMap(string: String): Map<String, Double> {
        try{
            val map = string.split(",")
                .map { it.split("=") }
                .map { it.first() to it.last().toDouble() }
                .toMap()
            return map
        }catch (e: Exception){
            throw IllegalArgumentException("Wrong format")
        }

    }

    fun mapToString(map: Map<String, Double>) = map.toString()
}