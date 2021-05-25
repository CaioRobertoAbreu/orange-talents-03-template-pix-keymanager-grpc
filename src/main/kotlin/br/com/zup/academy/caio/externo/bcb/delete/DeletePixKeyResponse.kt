package br.com.zup.academy.caio.externo.bcb.delete

import java.time.LocalDateTime

data class DeletePixKeyResponse(
    val key: String,
    val participant: String,
    val deletedAt: LocalDateTime

) {

}
