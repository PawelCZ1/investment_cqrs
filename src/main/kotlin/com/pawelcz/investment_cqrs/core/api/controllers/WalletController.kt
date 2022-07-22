package com.pawelcz.investment_cqrs.core.api.controllers

import com.pawelcz.investment_cqrs.core.api.dto.CreateWalletDTO
import com.pawelcz.investment_cqrs.core.api.services.WalletService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/wallets")
class WalletController(private val walletService: WalletService) {

    @PostMapping
    fun createWallet(@RequestBody createWalletDTO: CreateWalletDTO) = walletService.createWallet(createWalletDTO)

    @GetMapping
    fun getAllWallets() = walletService.getAllWallets()
}