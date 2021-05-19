package br.com.zup.academy.caio.validacao

import br.com.zup.academy.caio.validacao.custom.ChavePix
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
@ChavePix
class ValidaRequisicao(

    @field:NotBlank
    val codigoInterno: String,
    @field:NotBlank
    val tipoChave: String,
    @field:Size(max = 77)
    val valor: String,
    @field:NotBlank
    val tipoConta: String) {

}