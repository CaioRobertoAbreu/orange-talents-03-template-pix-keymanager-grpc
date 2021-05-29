package br.com.zup.academy.caio.chavepix.consulta

import br.com.zup.academy.caio.ConsultaChaveRequest
import br.com.zup.academy.caio.ConsultaChaveServiceGrpc
import br.com.zup.academy.caio.TipoChave
import br.com.zup.academy.caio.TipoConta
import br.com.zup.academy.caio.chavepix.ChavePixRepository
import br.com.zup.academy.caio.chavepix.cria.ChavePix
import br.com.zup.academy.caio.externo.bcb.ChavePixBCBExterno
import br.com.zup.academy.caio.externo.bcb.consulta.PixKeyDetailsResponse
import br.com.zup.academy.caio.externo.bcb.cria.AccountType
import br.com.zup.academy.caio.externo.bcb.cria.BankAccount
import br.com.zup.academy.caio.externo.bcb.cria.Owner
import br.com.zup.academy.caio.externo.bcb.cria.OwnerType
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class ConsultaChavePixControllerTest(
    @Inject val repository: ChavePixRepository,
    @Inject val client: ConsultaChaveServiceGrpc.ConsultaChaveServiceBlockingStub
) {

    private lateinit var chaveSalva: ChavePix

    @Inject
    private lateinit var clientBCB: ChavePixBCBExterno

    @BeforeEach
    fun setup() {
        chaveSalva = repository.save(chavePix(TipoChave.EMAIL, "maraja@legados.com"))
    }

    @AfterEach
    internal fun tearDown() {
        repository.deleteAll()
    }

    @Test
    fun `deve retornar uma chave pix para busca por cliente`() {
        //Cenario
        val clientId = ConsultaChaveRequest.ClienteId.newBuilder()
            .setClienteId(chaveSalva.clienteId)
            .setPixId(chaveSalva.pixId)
            .build()

        val request = ConsultaChaveRequest.newBuilder()
            .setClienteId(clientId)
            .build()

        `when`(clientBCB.consultarChave(chaveSalva.valor))
            .thenReturn(HttpResponse.ok())

        //Acao
        val chavePixEncontrada = client.consultarChave(request)

        //Verificacao
        assertEquals(chaveSalva.clienteId, chavePixEncontrada.clienteId)
        assertEquals(chaveSalva.valor, chavePixEncontrada.valor)

    }

    @Test
    fun `deve retornar excecao para chave nao pertencente ao cliente`() {
        //Cenario
        val clientId = ConsultaChaveRequest.ClienteId.newBuilder()
            .setClienteId("73617678-u91i-9211-y4q6-ue18ei1eiw19")
            .setPixId(chaveSalva.pixId) //chave existe e nao pertence ao cliente
            .build()

        val request = ConsultaChaveRequest.newBuilder()
            .setClienteId(clientId)
            .build()

        //Acao
        val error = assertThrows<StatusRuntimeException> {
            client.consultarChave(request)
        }

        //Verificacao
        with(error) {
            assertEquals(Status.NOT_FOUND.code, error.status.code)
            assertEquals("chave nao encontrada ou nao pertencente ao cliente", error.status.description)
        }

    }

    @Test
    fun `deve retornar excecao para chave nao encontrada`() {
        //Cenario
        val clientId = ConsultaChaveRequest.ClienteId.newBuilder()
            .setClienteId(chaveSalva.clienteId)
            .setPixId("hqdh7g7dq1ed1") //cleinte existe mas chave nao existe
            .build()

        val request = ConsultaChaveRequest.newBuilder()
            .setClienteId(clientId)
            .build()

        //Acao
        val error = assertThrows<StatusRuntimeException> {
            client.consultarChave(request)
        }

        //Verificacao
        with(error) {
            assertEquals(Status.NOT_FOUND.code, error.status.code)
            assertEquals("chave nao encontrada ou nao pertencente ao cliente", error.status.description)
        }

    }

    @Test
    fun `deve retornar uma excecao para chave existente no bd mas nao existente no bacen`() {
        //Cenario
        val clientId = ConsultaChaveRequest.ClienteId.newBuilder()
            .setClienteId(chaveSalva.clienteId)
            .setPixId(chaveSalva.pixId)
            .build()

        val request = ConsultaChaveRequest.newBuilder()
            .setClienteId(clientId)
            .build()

        `when`(clientBCB.consultarChave(chaveSalva.valor))
            .thenReturn(HttpResponse.notFound())

        //Acao
        val error = assertThrows<StatusRuntimeException> {
            client.consultarChave(request)
        }


        //Verificacao
        with(error) {
            assertEquals(Status.NOT_FOUND.code, error.status.code)
            assertEquals("chave nao encontrada ou nao pertencente ao cliente", error.status.description)
        }

    }

    @Test
    fun `deve retornar excecao para parametros invalido quando buscar por clienteId`(){
        //Cenario
        val clientId = ConsultaChaveRequest.ClienteId.newBuilder().build()

        val request = ConsultaChaveRequest.newBuilder()
            .setClienteId(clientId)
            .build()


        //Acao
        val error = assertThrows<StatusRuntimeException> {
            client.consultarChave(request)
        }


        //Verificacao
        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, error.status.code)
            assertEquals("Parametros invalidos", error.status.description)
        }
    }



    @Test
    fun `deve retornar uma chave pix para busca por chave`() {
        //Cenario
        val request = ConsultaChaveRequest.newBuilder()
            .setChave(chaveSalva.valor)
            .build()

        //Acao
        val chavePixEncontrada = client.consultarChave(request)

        //Verificacao
        assertEquals(chaveSalva.clienteId, chavePixEncontrada.clienteId)
        assertEquals(chaveSalva.valor, chavePixEncontrada.valor)

    }

    @Test
    fun `deve retornar uma chave pix que nao foi encontrada no bd mas foi encontrada no bacen`() {
        //Cenario
        val request = ConsultaChaveRequest.newBuilder()
            .setChave("+5513999997777")
            .build()

        val responseBacen = pixKeyDetailsResponse()
        `when`(clientBCB.consultarChave(request.chave))
            .thenReturn(HttpResponse.ok(responseBacen))

        //Acao
        val chavePixEncontrada = client.consultarChave(request)

        //Verificacao
        assertEquals(responseBacen.owner.taxIdNumber, chavePixEncontrada.cpf)
        assertEquals(responseBacen.bankAccount.accountType, AccountType.converte(chavePixEncontrada.tipoConta))
        assertEquals(PixKeyDetailsResponse.toTipoChave(responseBacen.keyType), chavePixEncontrada.tipoChave)
        assertEquals(responseBacen.key, chavePixEncontrada.valor)

    }

    @Test
    fun `deve retornar excecao para chave pix nao encontrada no bd e no bacen`() {
        //Cenario
        val request = ConsultaChaveRequest.newBuilder()
            .setChave("+5513999997777")
            .build()

        `when`(clientBCB.consultarChave(request.chave))
            .thenReturn(HttpResponse.notFound())

        //Acao
        val error = assertThrows<StatusRuntimeException> {
            client.consultarChave(request)
        }


        //Verificacao
        with(error) {
            assertEquals(Status.NOT_FOUND.code, error.status.code)
            assertEquals("chave nao encontrada ou nao pertencente ao cliente bcb", error.status.description)
        }

    }

    @Test
    fun `deve retornar excecao para filtro invalido`() {
        //Cenario
        val request = ConsultaChaveRequest.newBuilder()
            .build()

        //Acao
        val error = assertThrows<StatusRuntimeException> {
            client.consultarChave(request)
        }

        //Verificacao
        with(error){
            assertEquals(Status.FAILED_PRECONDITION.code, error.status.code)
            assertEquals("Filtro invalido", error.status.description)
        }

    }

    @Test
    fun `deve retornar excecao para parametros invalido quando buscar por chave`(){
        //Cenario

        val request = ConsultaChaveRequest.newBuilder()
            .setChave("")
            .build()


        //Acao
        val error = assertThrows<StatusRuntimeException> {
            client.consultarChave(request)
        }


        //Verificacao
        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, error.status.code)
            assertEquals("Parametros invalidos", error.status.description)
        }
    }

    @MockBean(ChavePixBCBExterno::class)
    fun consultaClientExternoBacen(): ChavePixBCBExterno? {

        return Mockito.mock(ChavePixBCBExterno::class.java)
    }

}

fun chavePix(tipoChave: TipoChave, valor: String): ChavePix {

    return ChavePix(
        tipoChave, valor, TipoConta.CONTA_CORRENTE, "1000", "0001", "Maraja dos Legados",
        "98516230082", "5260263c-a3c1-4727-ae32-3bdb2538841b", LocalDateTime.now()
    )
}

@Factory
class ConsultarChaveClient() {

    @Singleton
    fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): ConsultaChaveServiceGrpc.ConsultaChaveServiceBlockingStub? {

        return ConsultaChaveServiceGrpc.newBlockingStub(channel)
    }
}

fun pixKeyDetailsResponse(): PixKeyDetailsResponse {
    val bankAccount = BankAccount("60701190", "1000", "0002", AccountType.CACC.name)
    val owner = Owner(OwnerType.NATURAL_PERSON.name, "Principe dos Oceanos", "13419141009")

    return PixKeyDetailsResponse("PHONE", "+5513999997777", bankAccount, owner, LocalDateTime.now())
}