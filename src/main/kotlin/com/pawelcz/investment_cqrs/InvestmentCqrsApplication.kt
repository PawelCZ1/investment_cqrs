package com.pawelcz.investment_cqrs

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext

@SpringBootApplication
class InvestmentCqrsApplication{

}

fun main(args: Array<String>) {
    runApplication<InvestmentCqrsApplication>(*args)

}
