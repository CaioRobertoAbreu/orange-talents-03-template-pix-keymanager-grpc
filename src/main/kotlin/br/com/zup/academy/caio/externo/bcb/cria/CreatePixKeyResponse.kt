package br.com.zup.academy.caio.externo.bcb.cria

import java.time.LocalDateTime

data class CreatePixKeyResponse(
    val keyType: String,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
) {

}
