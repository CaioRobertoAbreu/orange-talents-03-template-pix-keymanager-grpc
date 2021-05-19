package br.com.zup.academy.caio.externo

import br.com.zup.academy.caio.CadastraChaveRequest
import br.com.zup.academy.caio.chavepix.ChavePix
import io.grpc.Status
import io.micronaut.http.client.exceptions.HttpClientResponseException
import javax.inject.Singleton

@Singleton
class ConsultaCorrentista(
    val client: ConsultaCorrentistaClient
) {

    fun consultaCliente(request: CadastraChaveRequest): ChavePix {

        client.consultaCliente(request.codigoInterno, request.tipoConta.name)
            .apply {
                if(this == null) {
                    throw IllegalArgumentException("Dados inválidos")
                } else {
                    return this.toChavePix(request)
                }
            }

    }
}