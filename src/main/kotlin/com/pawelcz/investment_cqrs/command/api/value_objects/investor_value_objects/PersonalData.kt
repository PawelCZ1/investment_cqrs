package com.pawelcz.investment_cqrs.command.api.value_objects.investor_value_objects


import com.pawelcz.investment_cqrs.core.api.exceptions.WrongArgumentException
import java.time.LocalDate

data class PersonalData(
    val name: String,
    val surname: String,
    val dateOfBirth: LocalDate
){
    init {
        if(dateOfBirth.isAfter(LocalDate.now().minusYears(18)))
            throw WrongArgumentException("This person is too young to be registered as investor")
    }

}
