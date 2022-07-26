package com.pawelcz.investment_cqrs.core.api.config

import com.pawelcz.investment_cqrs.core.api.security.AudienceValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jwt.*

@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Value("\${auth0.audience}")
    private val audience: String = String()

    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private val issuer: String = String()
    @Bean
    fun jwtDecoder(): JwtDecoder {
        val jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuer) as NimbusJwtDecoder
        val audienceValidator: OAuth2TokenValidator<Jwt> = AudienceValidator(audience)
        val withIssuer: OAuth2TokenValidator<Jwt> = JwtValidators.createDefaultWithIssuer(issuer)
        val withAudience: OAuth2TokenValidator<Jwt> = DelegatingOAuth2TokenValidator(withIssuer, audienceValidator)
        jwtDecoder.setJwtValidator(withAudience)
        return jwtDecoder
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .mvcMatchers(HttpMethod.POST,"/investors").authenticated()
            .mvcMatchers(HttpMethod.POST,"/investments").authenticated()
            .mvcMatchers(HttpMethod.POST,"/investors/wallets").authenticated()
            .mvcMatchers(HttpMethod.POST,"/investors/investment/register").authenticated()
            .mvcMatchers(HttpMethod.PATCH,"/*").authenticated()
            .mvcMatchers("/graphql").authenticated()
            .antMatchers("/actuator/*").authenticated()
            .and()
            .oauth2ResourceServer().jwt()
    }

}