package br.com.zup.academy.caio.chavepix.cria

import br.com.zup.academy.caio.CriaChaveServiceGrpc
import br.com.zup.academy.caio.TipoChave
import br.com.zup.academy.caio.TipoConta
import br.com.zup.academy.caio.chavepix.ChavePixRepository
import br.com.zup.academy.caio.externo.bcb.*
import br.com.zup.academy.caio.externo.bcb.cria.*
import br.com.zup.academy.caio.externo.erp_itau.ConsultaCorrentistaExterno
import br.com.zup.academy.caio.externo.erp_itau.ConsultaCorrentistaResponse
import br.com.zup.academy.caio.externo.erp_itau.Titular
import com.google.rpc.BadRequest
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
class CriaChaveControllerTest(
    @Inject val client: CriaChaveServiceGrpc.CriaChaveServiceBlockingStub,
    val repository: ChavePixRepository){

    @Inject
    lateinit var externoItau: ConsultaCorrentistaExterno
    @Inject
    lateinit var externoBCB: ChavePixBCBExterno

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `deve cadastrar uma nova chave pix`() {
        //Cenario
        val request = CriaRequestBuilder().comValoresPadrao().now()

        val response = ConsultaCorrentistaResponse(
            tipo = "CONTA_CORRENTE", agencia = "0001", numero ="291900",
            titular = Titular("5260263c-a3c1-4727-ae32-3bdb2538841b", "Maraja dos Legados", "12345678910")
        )

        `when`(externoItau.consultaCliente(request.codigoInterno, request.tipoConta.toString()))
            .thenReturn(HttpResponse.ok(response))

        val pixKeyReqeust = CreatePixKeyRequest(request.tipoChave.name, request.valor,
        BankAccount("60701190", response.agencia, response.numero, AccountType.converte(response.tipo)),
        Owner(OwnerType.NATURAL_PERSON.name, response.nome, response.cpf)
        )

        val responseCriaChaveBCB = CreatePixKeyResponse(request.tipoChave.name, request.valor,
        pixKeyReqeust.bankAccount, pixKeyReqeust.owner, LocalDateTime.now())

        `when`(externoBCB.criarChave(pixKeyReqeust))
            .thenReturn(HttpResponse.created(responseCriaChaveBCB))

        //Acao
        val chaveRegistrada = client.registrarChave(request)

        //Verificação
        with(chaveRegistrada) {
            Assertions.assertNotNull(this.pixId)
            assertTrue(repository.existsByValor(request.valor))
        }
    }

    @Test
    fun `deve retornar exceção para tipo de conta incorreta quando retornar null`() {
        //Cenario
        val request = CriaRequestBuilder()
            .comValoresPadrao()
            .alterarConta(TipoConta.CONTA_POUPANCA_VALUE).now()

        //Acao
        `when`(externoItau.consultaCliente(request.codigoInterno, request.tipoConta.toString()))
            .thenReturn(null)

        val error = assertThrows<StatusRuntimeException> {
            client.registrarChave(request)
        }

        //Verificacao
        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("Dados inválidos", this.status.description)
        }
    }

    @Test
    fun `deve retornar exceção para tipo de conta incorreta quando retornar HttpClientResponseException`() {
        //Cenario
        val request = CriaRequestBuilder()
            .comValoresPadrao()
            .alterarConta(TipoConta.CONTA_POUPANCA_VALUE).now()

        //Acao
        `when`(externoItau.consultaCliente(request.codigoInterno, request.tipoConta.toString()))
            .thenThrow(HttpClientResponseException::class.java)

        val error = assertThrows<StatusRuntimeException> {
            client.registrarChave(request)
        }

        //Verificacao
        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("Dados invalidos", this.status.description)
        }
    }

    @Test
    fun `deve retornar excecao para campos obrigatorios que sao enviados vazio`() {
        //Cenario
        val request = CriaRequestBuilder()
            .comCamposObrigatoriosInvalidos()
            .now()

        //Acao
        val error = assertThrows<StatusRuntimeException> {
            client.registrarChave(request)
        }

        //Verificacao
        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("Parametros invalidos", this.status.description)

            val details = StatusProto.fromThrowable(this)?.detailsList
                ?.get(0)!!
                .unpack(BadRequest::class.java)
                .fieldViolationsList.map { violacao ->
                    Pair(violacao.field, violacao.description)
                }

            println(details)
            assertTrue(details.contains(Pair("codigoInterno", "não deve estar em branco")))
            assertTrue(details.contains(Pair("tipoChave", "não deve ser nulo")))
            assertTrue(details.contains(Pair("tipoConta", "não deve ser nulo")))
        }
    }

    @Test
    fun `deve retornar excecao com chave tipo cpf incorreta`(){
        //Cenario
        val request = CriaRequestBuilder()
            .comValoresPadrao()
            .comTipoDeChave(1)
            .comValorChave("123456789")
            .now()

        //Acao
        val error = assertThrows<StatusRuntimeException> {
            client.registrarChave(request)
        }

        //Verificacao
        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("Parametros invalidos", this.status.description)
        }
    }

    @Test
    fun `deve retornar excecao com chave tipo celular incorreta`(){
        //Cenario
        val request = CriaRequestBuilder()
            .comTipoDeChave(1)
            .comValorChave("13997979797")
            .now()

        //Acao
        val error = assertThrows<StatusRuntimeException> {
            client.registrarChave(request)
        }

        //Verificacao
        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("Parametros invalidos", this.status.description)
        }
    }

    @Test
    fun `deve retornar excecao com chave tipo email incorreta`(){
        //Cenario
        val request = CriaRequestBuilder()
            .comValoresPadrao()
            .comTipoDeChave(3)
            .comValorChave("email.invalido")
            .now()

        //Acao
        val error = assertThrows<StatusRuntimeException> {
            client.registrarChave(request)
        }

        //Verificacao
        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("Parametros invalidos", this.status.description)
        }
    }

    @Test
    fun `deve retornar excecao com chave tipo aleatoria incorreta`(){
        //Cenario
        val request = CriaRequestBuilder()
            .comValoresPadrao()
            .comTipoDeChave(TipoChave.CHAVE_ALEATORIA_VALUE)
            .now()

        //Acao
        val error = assertThrows<StatusRuntimeException> {
            client.registrarChave(request)
        }

        //Verificacao
        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("Parametros invalidos", this.status.description)
        }
    }

    @Test
    fun `deve retornar excecao para chave duplicada`(){
        //Cenario
        val request = CriaRequestBuilder()
            .comValoresPadrao()
            .now()

        val response = ConsultaCorrentistaResponse(
            tipo = "CONTA_CORRENTE", agencia = "0001", numero ="291900",
            titular = Titular("5260263c-a3c1-4727-ae32-3bdb2538841b", "Maraja dos Legados", "12345678910")
        )

        `when`(externoItau.consultaCliente(request.codigoInterno, request.tipoConta.toString()))
            .thenReturn(HttpResponse.ok(response))

        val createPixRequest = CreatePixKeyRequest(request.tipoChave.name, request.valor,
            BankAccount("60701190", response.agencia, response.numero, AccountType.CACC.name,),
            Owner(OwnerType.NATURAL_PERSON.name, response.nome, response.cpf)
        )

        val responseCriaChaveBCB = CreatePixKeyResponse(request.tipoChave.name, request.valor,
        createPixRequest.bankAccount, createPixRequest.owner, LocalDateTime.now())

        `when`(externoBCB.criarChave(createPixRequest))
            .thenReturn(HttpResponse.created(responseCriaChaveBCB))

        client.registrarChave(request)

        //Acao
        val error = assertThrows<StatusRuntimeException> {
            client.registrarChave(request)
        }

        //Verificacao
        with(error){
            assertEquals(Status.ALREADY_EXISTS.code, this.status.code)
            assertEquals("Chave ja cadastrada", this.status.description)
        }
    }

    @Test
    fun `deve retornar excecao quando encontrar erro ao cadastrar a mesma no bacen`(){
        //Cenario
        val request = CriaRequestBuilder().comValoresPadrao().now()

        val response = ConsultaCorrentistaResponse(
            tipo = "CONTA_CORRENTE", agencia = "0001", numero ="291900",
            titular = Titular("5260263c-a3c1-4727-ae32-3bdb2538841b", "Maraja dos Legados", "12345678910")
        )

        `when`(externoItau.consultaCliente(request.codigoInterno, request.tipoConta.toString()))
            .thenReturn(HttpResponse.ok(response))

        val pixKeyReqeust = CreatePixKeyRequest(request.tipoChave.name, request.valor,
            BankAccount("60701190", response.agencia, response.numero, AccountType.converte(response.tipo)),
            Owner(OwnerType.NATURAL_PERSON.name, response.nome, response.cpf))

        `when`(externoBCB.criarChave(pixKeyReqeust))
            .thenReturn(HttpResponse.status(HttpStatus.UNPROCESSABLE_ENTITY))

        //Acao
        val error = assertThrows<StatusRuntimeException> {
            client.registrarChave(request)
        }

        //Verificação
        with(error) {
            assertEquals(Status.FAILED_PRECONDITION.code, this.status.code)
            assertEquals("Erro ao cadastrar chave no Bacen", this.status.description)
            assertFalse(repository.existsByValor(request.valor))
        }
    }

    @MockBean(ConsultaCorrentistaExterno::class)
    fun mockConsultaClientExternoItau(): ConsultaCorrentistaExterno? {
        return Mockito.mock(ConsultaCorrentistaExterno::class.java)
    }

    @MockBean(ChavePixBCBExterno::class)
    fun mockChavePixBCBExterno(): ChavePixBCBExterno? {
        return Mockito.mock(ChavePixBCBExterno::class.java)
    }


}

@Factory
class criaChaveClient{

    @Singleton
    fun blockingStup(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): CriaChaveServiceGrpc.CriaChaveServiceBlockingStub? {

        return CriaChaveServiceGrpc.newBlockingStub(channel)
    }
}


