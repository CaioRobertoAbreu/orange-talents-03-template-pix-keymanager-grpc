package br.com.zup.academy.caio.externo.bcb

import java.time.LocalDateTime

data class ResponseCriaChaveBCB(
    val keyType: String,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
) {


}
