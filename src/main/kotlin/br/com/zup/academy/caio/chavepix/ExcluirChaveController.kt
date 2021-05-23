package br.com.zup.academy.caio.chavepix

import br.com.zup.academy.caio.ExcluiChaveServiceGrpc
import br.com.zup.academy.caio.ExclusaoChaveRequest
import br.com.zup.academy.caio.ExclusaoChaveResponse
import br.com.zup.academy.caio.handler.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class ExcluirChaveController(
    @Inject val service: ChavePixService
): ExcluiChaveServiceGrpc.ExcluiChaveServiceImplBase() {

    override fun excluirChave(request: ExclusaoChaveRequest,
        responseObserver: StreamObserver<ExclusaoChaveResponse>?) {

        val excluir = request.toExluirChave()

        val response = service.excluirChave(excluir).run {
            ExclusaoChaveResponse.newBuilder()
                .setResponse("Chave excluída com sucesso!")
                .build()
        }

        responseObserver?.onNext(response)
        responseObserver?.onCompleted()

    }
}