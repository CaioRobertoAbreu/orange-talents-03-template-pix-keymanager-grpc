package br.com.zup.academy.caio.chavepix.consulta

import br.com.zup.academy.caio.ConsultaChaveRequest
import br.com.zup.academy.caio.ConsultaChaveResponse
import br.com.zup.academy.caio.ConsultaChaveServiceGrpc
import br.com.zup.academy.caio.chavepix.ChavePixRepository
import br.com.zup.academy.caio.externo.bcb.ChavePixBCBExterno
import br.com.zup.academy.caio.handler.ErrorHandler
import io.grpc.stub.StreamObserver
import io.micronaut.validation.Validated
import io.micronaut.validation.validator.Validator
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@ErrorHandler
@Validated
class ConsultaChavePixController(
    @Inject val repository: ChavePixRepository,
    @Inject val clienteBCB: ChavePixBCBExterno,
    @Inject val validator: Validator) : ConsultaChaveServiceGrpc.ConsultaChaveServiceImplBase() {



    override fun consultarChave(
        request: ConsultaChaveRequest,
        responseObserver: StreamObserver<ConsultaChaveResponse>?) {

        val filtro = request.toModel(validator)

        val response = filtro.filtra(repository, clienteBCB).run {
            return@run this.converteToResponse()
        }

        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }


}

