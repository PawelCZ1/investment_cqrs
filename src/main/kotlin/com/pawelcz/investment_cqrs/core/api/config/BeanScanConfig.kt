package com.pawelcz.investment_cqrs.core.api.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = ["com.pawelcz.investment_cqrs"])
class BeanScanConfig {
}