package br.com.zup.academy.caio.chavepix

import br.com.zup.academy.caio.CriaChaveServiceGrpc
import br.com.zup.academy.caio.TipoChave
import br.com.zup.academy.caio.TipoConta
import br.com.zup.academy.caio.chavepix.ChavePixRepository
import br.com.zup.academy.caio.chavepix.CriaRequestBuilder
import br.com.zup.academy.caio.externo.ConsultaCorrentistaClient
import br.com.zup.academy.caio.externo.ConsultaCorrentistaResponse
import br.com.zup.academy.caio.externo.Titular
import com.google.rpc.BadRequest
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
class CriaChaveControllerTest(
    @Inject val client: CriaChaveServiceGrpc.CriaChaveServiceBlockingStub,
    val repository: ChavePixRepository){

    @Inject
    lateinit var externo: ConsultaCorrentistaClient

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `deve cadastrar uma nova chave pix`() {
        //Cenario
        val request = CriaRequestBuilder().comValoresPadrao().now()

        val response = ConsultaCorrentistaResponse(
            tipo = "CONTA_POUPANCA", agencia = "0001", numero ="291900",
            titular = Titular("5260263c-a3c1-4727-ae32-3bdb2538841b", "Maraja dos Legados", "12345678910"))

        `when`(externo.consultaCliente(request.codigoInterno, request.tipoConta.toString()))
            .thenReturn(response)

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
        `when`(externo.consultaCliente(request.codigoInterno, request.tipoConta.toString()))
            .thenReturn(null)

        val error = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
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
        `when`(externo.consultaCliente(request.codigoInterno, request.tipoConta.toString()))
            .thenThrow(HttpClientResponseException::class.java)

        val error = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            client.registrarChave(request)
        }

        //Verificacao
        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("Dados inválidos", this.status.description)
        }
    }

    @Test
    fun `deve retornar excecao para campos obrigatorios que sao enviados vazio`() {
        //Cenario
        val request = CriaRequestBuilder()
            .comCamposObrigatoriosInvalidos()
            .now()

        //Acao
        val error = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            client.registrarChave(request)
        }

        //Verificacao
        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("Parâmetros inválidos", this.status.description)

            val details = StatusProto.fromThrowable(this)?.detailsList
                ?.get(0)!!
                .unpack(BadRequest::class.java)
                .fieldViolationsList.map { violacao ->
                    Pair(violacao.field, violacao.description)
                }

            assertTrue(details.contains(Pair("novaChavePix", "Valor da chave inválido")))
            assertTrue(details.contains(Pair("codigoInterno", "must not be blank")))
        }
    }

    @Test
    fun `deve retornar excecao com chave tipo cpf incorreta`(){
        //Cenario
        val request = CriaRequestBuilder()
            .comTipoDeChave(0)
            .comValorChave("123456789")
            .now()

        //Acao
        val error = assertThrows<StatusRuntimeException> {
            client.registrarChave(request)
        }

        //Verificacao
        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("Parâmetros inválidos", this.status.description)
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
            assertEquals("Parâmetros inválidos", this.status.description)
        }
    }

    @Test
    fun `deve retornar excecao com chave tipo email incorreta`(){
        //Cenario
        val request = CriaRequestBuilder()
            .comTipoDeChave(2)
            .comValorChave("email.invalido")
            .now()

        //Acao
        val error = assertThrows<StatusRuntimeException> {
            client.registrarChave(request)
        }

        //Verificacao
        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("Parâmetros inválidos", this.status.description)
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
            assertEquals("Parâmetros inválidos", this.status.description)
        }
    }

    @Test
    fun `deve retornar excecao para chave duplicada`(){
        //Cenario
        val request = CriaRequestBuilder()
            .comValoresPadrao()
            .now()

        val response = ConsultaCorrentistaResponse(
            tipo = "CONTA_POUPANCA", agencia = "0001", numero ="291900",
            titular = Titular("5260263c-a3c1-4727-ae32-3bdb2538841b", "Maraja dos Legados", "12345678910"))

        `when`(externo.consultaCliente(request.codigoInterno, request.tipoConta.toString()))
            .thenReturn(response)
        client.registrarChave(request)

        //Acao
        val error = assertThrows<StatusRuntimeException> {
            client.registrarChave(request)
        }

        //Verificacao
        with(error){
            assertEquals(Status.ALREADY_EXISTS.code, this.status.code)
            assertEquals("Chave já cadastrada", this.status.description)
        }
    }

    @MockBean(ConsultaCorrentistaClient::class)
    fun mockConsultaClientExterno(): ConsultaCorrentistaClient? {
        return Mockito.mock(ConsultaCorrentistaClient::class.java)
    }
}



@Factory
class criaChaveClient{

    @Singleton
    fun blockingStup(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): CriaChaveServiceGrpc.CriaChaveServiceBlockingStub? {

        return CriaChaveServiceGrpc.newBlockingStub(channel)
    }
}

