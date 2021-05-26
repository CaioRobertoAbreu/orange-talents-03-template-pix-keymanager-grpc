package br.com.zup.academy.caio.chavepix.consulta

import br.com.zup.academy.caio.ConsultaChaveRequest
import br.com.zup.academy.caio.validacao_customizada.ConsultaChavePix
import io.micronaut.core.annotation.Introspected

@Introspected
@ConsultaChavePix
class ConsultaChave(val request: ConsultaChaveRequest) {

    var clienteId: String = request.clienteId
    val pixId: String = request.pixId
    val chavePix: String = request.chavePix

}