package com.pawelcz.investment_cqrs.command.api.value_objects.investment_value_objects

enum class Status(val isActive: Boolean) {
    ACTIVE(true), INACTIVE(false)
}