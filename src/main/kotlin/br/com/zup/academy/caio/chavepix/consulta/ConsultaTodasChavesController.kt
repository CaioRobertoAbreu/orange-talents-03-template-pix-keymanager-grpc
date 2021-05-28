package br.com.zup.academy.caio.chavepix.consulta

import br.com.zup.academy.caio.ConsultaTodasChavesGrpc
import br.com.zup.academy.caio.ListaChavesRequest
import br.com.zup.academy.caio.ListaChavesResponse
import br.com.zup.academy.caio.chavepix.ChavePixRepository
import br.com.zup.academy.caio.handler.ErrorHandler
import io.grpc.stub.StreamObserver
import io.micronaut.validation.Validated
import java.lang.IllegalArgumentException
import javax.inject.Inject
import javax.inject.Singleton

@Validated
@ErrorHandler
@Singleton
class ConsultaTodasChavesController(@Inject val repository: ChavePixRepository) :
    ConsultaTodasChavesGrpc.ConsultaTodasChavesImplBase() {

    override fun listarTodas(request: ListaChavesRequest, responseObserver: StreamObserver<ListaChavesResponse>?) {

        if (request.clienteId.isNullOrBlank()) {
            throw IllegalArgumentException("O campo clienteId deve ser preenchido")
        }

        val chaves = repository.findAllByClienteId(request.clienteId).map { chave ->
            ListaChavesResponse.Chave.newBuilder()
                .setPixId(chave.pixId)
                .setClienteId(chave.clienteId)
                .setTipoChave(chave.tipoChave.name)
                .setValor(chave.valor)
                .setTipoConta(chave.tipoConta.name)
                .setCriadoEm(chave.criadoEm.toString())
                .build()
        }

        val chavesList = ListaChavesResponse.newBuilder().addAllChaves(chaves).build()

        responseObserver?.onNext(chavesList)
        responseObserver?.onCompleted()

    }
}