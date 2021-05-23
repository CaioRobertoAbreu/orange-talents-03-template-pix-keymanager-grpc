package br.com.zup.academy.caio.chavepix

import br.com.zup.academy.caio.ExclusaoChaveRequest
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class ExcluirChave(
    @field:NotBlank
    val pixId: String,
    @field:NotBlank
    val clienteId: String
) {

}

fun ExclusaoChaveRequest.toExluirChave(): ExcluirChave {

    return ExcluirChave(this.pixId, this.clienteId)
}