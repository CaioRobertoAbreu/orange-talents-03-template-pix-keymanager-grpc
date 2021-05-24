package br.com.zup.academy.caio.chavepix

import br.com.zup.academy.caio.CadastraChaveRequest
import br.com.zup.academy.caio.CadastraChaveResponse
import br.com.zup.academy.caio.CriaChaveServiceGrpc
import br.com.zup.academy.caio.handler.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class CriaChaveController(
    @Inject private val chavePixService: ChavePixService
) : CriaChaveServiceGrpc.CriaChaveServiceImplBase() {

    override fun registrarChave(request: CadastraChaveRequest, responseObserver: StreamObserver<CadastraChaveResponse>) {

        val novaChavePix = request.toNovaChavePix()

        val chavePix = chavePixService.registra(novaChavePix)

        val response = with(chavePix){
            CadastraChaveResponse.newBuilder()
                .setClienteId(this.clienteId)
                .setPixId(this.pixId.toString())
                .build()
        }

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

}

