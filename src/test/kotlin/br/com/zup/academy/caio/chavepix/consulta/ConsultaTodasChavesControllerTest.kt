package br.com.zup.academy.caio.chavepix.consulta

import br.com.zup.academy.caio.ConsultaTodasChavesGrpc
import br.com.zup.academy.caio.ListaChavesRequest
import br.com.zup.academy.caio.TipoChave
import br.com.zup.academy.caio.TipoConta
import br.com.zup.academy.caio.chavepix.ChavePixRepository
import br.com.zup.academy.caio.chavepix.cria.ChavePix
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class ConsultaTodasChavesControllerTest(@Inject val client: ConsultaTodasChavesGrpc.ConsultaTodasChavesBlockingStub) {

    /*
        1. Happy path - OK
        2. Requisicao com dados invalidos - OK
        3. Retornar lista de chaves vazia - OK
     */

    @Inject
    private lateinit var repository: ChavePixRepository
    private lateinit var chavePix1: ChavePix
    private lateinit var chavePix2: ChavePix
    private lateinit var chaves: MutableList<ChavePix>

    @BeforeEach
    fun setup() {
        chavePix1 = ChavePix(
            TipoChave.EMAIL, "maraja@legados.com", TipoConta.CONTA_CORRENTE, "1000",
            "0001", "Maraja dos Legados", "98516230082", "5260263c-a3c1-4727-ae32-3bdb2538841b",
            LocalDateTime.now()
        )

        chavePix2 = ChavePix(
            TipoChave.CELULAR, "+5513999998888", TipoConta.CONTA_CORRENTE, "1000",
            "0001", "Maraja dos Legados", "98516230082", "5260263c-a3c1-4727-ae32-3bdb2538841b",
            LocalDateTime.now()
        )

        chaves = mutableListOf<ChavePix>(chavePix1, chavePix2)

        repository.saveAll(chaves)
    }

    @AfterEach
    internal fun tearDown() {
        repository.deleteAll()
    }

    @Test
    fun `deve retornar lista com todas as chaves`() {
        //Cenario
        val request = ListaChavesRequest.newBuilder()
            .setClienteId(chavePix1.clienteId)
            .build()

        //Acao
        val response = client.listarTodas(request)

        //Verificacao
        assertEquals(response.chavesCount, 2)
        assertEquals(chavePix1.clienteId, response.getChaves(0).clienteId )
        assertEquals(chavePix1.tipoChave.name, response.getChaves(0).tipoChave )
        assertEquals(chavePix1.valor, response.getChaves(0).valor )

        assertEquals(chavePix2.clienteId, response.getChaves(1).clienteId )
        assertEquals(chavePix2.tipoChave.name, response.getChaves(1).tipoChave )
        assertEquals(chavePix2.valor, response.getChaves(1).valor )
    }

    @Test
    fun `deve retornar excecao ao enviar requisicao sem clienteId`(){
        //Cenario
        val request = ListaChavesRequest.newBuilder().build()

        //Acao
        val error = assertThrows<StatusRuntimeException> {
            client.listarTodas(request)
        }

        //verificacao
        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, error.status.code)
            assertEquals("O campo clienteId deve ser preenchido", error.status.description)

        }
    }

    @Test
    fun `deve retornar lista vazia quando nao houver chaves cadastradas para clienteId informado`(){
        //Cenario
        val request = ListaChavesRequest.newBuilder()
            .setClienteId("12345678910")
            .build()

        //Acao
        val chaves = client.listarTodas(request)

        //Verificacao
        assertEquals(chaves.chavesCount, 0)

    }

}

@Factory
class consultaChaveClient() {

    @Singleton
    fun consultaChaveClient(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): ConsultaTodasChavesGrpc.ConsultaTodasChavesBlockingStub? {

        return ConsultaTodasChavesGrpc.newBlockingStub(channel)

    }
}

