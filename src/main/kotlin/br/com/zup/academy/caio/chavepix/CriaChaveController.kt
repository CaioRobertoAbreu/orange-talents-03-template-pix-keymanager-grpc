package br.com.zup.academy.caio.chavepix

import br.com.zup.academy.caio.CadastraChaveRequest
import br.com.zup.academy.caio.CadastraChaveResponse
import br.com.zup.academy.caio.KeyManagerServiceGrpc
import br.com.zup.academy.caio.handler.ErrorHandler
import io.grpc.stub.StreamObserver
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class CriaChaveController(
    @Inject private val novaChavePixService: NovaChavePixService
) : KeyManagerServiceGrpc.KeyManagerServiceImplBase() {

    override fun registrarChave(request: CadastraChaveRequest, responseObserver: StreamObserver<CadastraChaveResponse>) {

        val novaChavePix = request.toNovaChavePix()

        val chavePix = novaChavePixService.registra(novaChavePix)

        val response = with(chavePix){
            CadastraChaveResponse.newBuilder()
                .setClienteId(this.id_cliente)
                .setPixId(this.pixId.toString())
                .build()
        }

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

}

