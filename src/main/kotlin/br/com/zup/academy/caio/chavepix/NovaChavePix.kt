package br.com.zup.academy.caio.chavepix

import br.com.zup.academy.caio.CadastraChaveRequest
import br.com.zup.academy.caio.TipoChave
import br.com.zup.academy.caio.TipoConta
import br.com.zup.academy.caio.validacao_customizada.ChavePix
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Introspected
@ChavePix
data class NovaChavePix(
    @field:NotBlank
    val codigoInterno: String,
    @field:NotNull
    val tipoChave: TipoChave?,
    @field:Size(max = 77)
    val valor: String,
    @field:NotNull
    val tipoConta: TipoConta?
) {
}

fun CadastraChaveRequest.toNovaChavePix(): NovaChavePix {

    return NovaChavePix(
        codigoInterno = codigoInterno,

        tipoChave = when(this.tipoChave) {
            TipoChave.CHAVE_DESCONHECIDA -> null
            else -> TipoChave.valueOf(this.tipoChave.name)},

        valor =  this.valor,

        tipoConta = when(this.tipoConta){
            TipoConta.CONTA_DESCONHECIDA -> null
            else -> TipoConta.valueOf(this.tipoConta.name) })
}
