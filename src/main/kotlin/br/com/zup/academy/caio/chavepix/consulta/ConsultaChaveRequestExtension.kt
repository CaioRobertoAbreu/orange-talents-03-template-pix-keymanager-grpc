package br.com.zup.academy.caio.chavepix.consulta

import br.com.zup.academy.caio.ConsultaChaveRequest
import br.com.zup.academy.caio.ConsultaChaveRequest.FiltroCase.CHAVE
import br.com.zup.academy.caio.ConsultaChaveRequest.FiltroCase.CLIENTEID
import io.micronaut.validation.validator.Validator
import javax.validation.ConstraintViolationException
import javax.validation.Valid


fun ConsultaChaveRequest.toModel(validator: Validator): Filtro {

    val filtro = when (filtroCase) {
        CLIENTEID -> validaCleinteId(Filtro.ClienteId(this.clienteId.pixId, this.clienteId.clienteId))
        CHAVE -> validaChave(Filtro.Chave(this.chave))
        else -> Filtro.Invalido()
    }

    val violations = validator.validate(filtro)
    if (violations.isNotEmpty()) {
        throw ConstraintViolationException(violations)
    }

    return filtro
}

fun ConsultaChaveRequest.validaCleinteId(@Valid filtro: Filtro.ClienteId): Filtro.ClienteId {
    return filtro
}

fun ConsultaChaveRequest.validaChave(@Valid filtro: Filtro.Chave): Filtro.Chave {
    return filtro
}






