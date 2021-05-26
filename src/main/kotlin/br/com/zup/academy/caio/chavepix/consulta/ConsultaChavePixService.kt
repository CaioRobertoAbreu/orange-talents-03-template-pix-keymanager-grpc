package br.com.zup.academy.caio.chavepix.consulta

import br.com.zup.academy.caio.chavepix.ChavePixRepository
import br.com.zup.academy.caio.chavepix.cria.ChavePix
import br.com.zup.academy.caio.exceptions.ChavePixNotFound
import br.com.zup.academy.caio.externo.bcb.ChavePixBCBExterno
import io.grpc.Status
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Valid

@Singleton
@Validated
class ConsultaChavePixService {

    @Inject
    private lateinit var repository: ChavePixRepository
    @Inject
    private lateinit var clienteBCB: ChavePixBCBExterno


    fun buscarChaves(@Valid consultaChave: ConsultaChave): ChavePix {

        val chaveEncontrada = repository.findByPixIdAndClienteId(consultaChave.pixId, consultaChave.clienteId)

        return chaveEncontrada.run {
            if (this.isEmpty && consultaChave.pixId.isBlank() && !consultaChave.clienteId.isBlank()) {
                throw ChavePixNotFound("Chave nao encontrada ou nao pertencente ao cliente")
            }

            if (this.isEmpty && !consultaChave.chavePix.isBlank()) {
                return@run consultarChaveBCB(clienteBCB, consultaChave.chavePix)
            }

            if (this.isPresent) {
                return verificarSeChaveEValida(clienteBCB, this.get())
            }

            throw Status.UNKNOWN.asRuntimeException()
        }

    }
}

fun verificarSeChaveEValida(clienteBCB: ChavePixBCBExterno, chavePix: ChavePix): ChavePix {
    clienteBCB.consultarChave(chavePix.valor).also { response ->
        if (response.status.code != HttpStatus.OK.code) {
            throw IllegalStateException("Chave invalida")
        }
        return chavePix
    }
}

fun consultarChaveBCB(clienteBCB: ChavePixBCBExterno, chavePix: String): ChavePix {
    clienteBCB.consultarChave(chavePix).also { response ->
        if (response.status.code != HttpStatus.OK.code) {
            throw ChavePixNotFound("Chave nao encontrada")
        }

        return response.body().toChavePix()
    }
}

