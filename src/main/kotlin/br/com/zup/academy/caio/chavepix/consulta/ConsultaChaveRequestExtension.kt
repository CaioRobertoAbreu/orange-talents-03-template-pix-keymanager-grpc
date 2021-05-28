package br.com.zup.academy.caio.chavepix.consulta

import br.com.zup.academy.caio.ConsultaChaveRequest
import br.com.zup.academy.caio.ConsultaChaveRequest.FiltroCase.CHAVE
import br.com.zup.academy.caio.ConsultaChaveRequest.FiltroCase.CLIENTEID
import io.micronaut.validation.Validated
import io.micronaut.validation.validator.Validator
import javax.inject.Singleton
import javax.validation.ConstraintViolationException
import javax.validation.Valid


fun ConsultaChaveRequest.toModel(validator: Validator): Filtro {

    val filtro = when (filtroCase) {
        CLIENTEID -> validaCleinteId(Filtro.clienteId(this.clienteId.pixId, this.clienteId.clienteId))
        CHAVE -> validaChave(Filtro.chave(this.chave))
        else -> Filtro.Invalido()
    }

    val violations = validator.validate(filtro)
    if (violations.isNotEmpty()) {
        throw ConstraintViolationException(violations)
    }

    return filtro
}

fun ConsultaChaveRequest.validaCleinteId(@Valid filtro: Filtro.clienteId): Filtro.clienteId {
    return filtro
}

fun ConsultaChaveRequest.validaChave(@Valid filtro: Filtro.chave): Filtro.chave {
    return filtro
}






