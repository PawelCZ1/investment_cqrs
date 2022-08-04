package com.pawelcz.investment_cqrs.core.api.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class WrongArgumentException(message: String) : RuntimeException(message)