package com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects


import java.time.LocalDate

data class PersonalData(
    val name: String,
    val surname: String,
    val dateOfBirth: LocalDate
){

}
