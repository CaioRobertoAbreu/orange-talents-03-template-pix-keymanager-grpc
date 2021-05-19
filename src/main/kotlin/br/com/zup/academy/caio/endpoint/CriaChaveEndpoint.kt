package br.com.zup.academy.caio.endpoint

import br.com.zup.academy.caio.CadastraChaveRequest
import br.com.zup.academy.caio.CadastraChaveResponse
import br.com.zup.academy.caio.KeyManagerServiceGrpc
import br.com.zup.academy.caio.chavepix.ChavePix
import br.com.zup.academy.caio.chavepix.ChavePixRepository
import br.com.zup.academy.caio.exceptions.ChavePixAlreadyExists
import br.com.zup.academy.caio.externo.ConsultaCorrentista
import br.com.zup.academy.caio.externo.ConsultaCorrentistaClient
import br.com.zup.academy.caio.externo.ConsultaCorrentistaResponse
import br.com.zup.academy.caio.handler.ErrorHandler
import br.com.zup.academy.caio.validacao.ValidaRequisicao
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.Validation
import javax.validation.Validator

@Singleton
@Validated
@ErrorHandler
class CriaChaveEndpoint(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val client: ConsultaCorrentista) : KeyManagerServiceGrpc.KeyManagerServiceImplBase() {

    override fun registrarChave(request: CadastraChaveRequest, responseObserver: StreamObserver<CadastraChaveResponse>) {


        validar(ValidaRequisicao(request.codigoInterno, request.tipoChave.name, request.valor, request.tipoConta.name))

        if(chavePixRepository.existsByValor(request.valor)){
            throw ChavePixAlreadyExists("Chave já cadastrada")
        }

        val novaChavePix = client.consultaCliente(request)

        val response = chavePixRepository.save(novaChavePix)
            .let { chavePix ->
                CadastraChaveResponse.newBuilder()
                    .setPixId(chavePix?.pixId.toString())
                    .build()
            }

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    fun validar(@Valid request: ValidaRequisicao){

    }
}

