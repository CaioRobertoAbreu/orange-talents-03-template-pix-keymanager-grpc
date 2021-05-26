package br.com.zup.academy.caio.chavepix.consulta

import br.com.zup.academy.caio.ConsultaChaveRequest
import br.com.zup.academy.caio.ConsultaChaveResponse
import br.com.zup.academy.caio.ConsultaChaveServiceGrpc
import br.com.zup.academy.caio.handler.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class ConsultaChaveController(
    @Inject
    private val consultaChavePix: ConsultaChavePixService
) : ConsultaChaveServiceGrpc.ConsultaChaveServiceImplBase() {

    override fun consultarChave(request: ConsultaChaveRequest,
                                responseObserver: StreamObserver<ConsultaChaveResponse>) {


        val response = consultaChavePix.buscarChaves(request.toConsultaChave()).run {

            ConsultaChaveResponse.newBuilder()
                .setPixId(this.pixId)
                .setClienteId(this.clienteId)
                .setTipoChave(this.tipoChave.name)
                .setValor(this.valor)
                .setNome(this.nome)
                .setCpf(this.cpf)
                .setInstituicaoFinanceira("")
                .setAgencia(this.agencia)
                .setNumero(this.numero)
                .setTipoConta(this.tipoConta.name)
                .setCriadoEm(this.criadoEm.toString())
                .build()
        }

        responseObserver.onNext(response)
        responseObserver.onCompleted()


    }
}

fun ConsultaChaveRequest.toConsultaChave(): ConsultaChave {

    return ConsultaChave(this)
}


