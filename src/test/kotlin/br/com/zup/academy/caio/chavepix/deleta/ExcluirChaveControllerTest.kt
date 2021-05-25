package br.com.zup.academy.caio.chavepix.cria

import br.com.zup.academy.caio.ExcluiChaveServiceGrpc
import br.com.zup.academy.caio.ExclusaoChaveRequest
import br.com.zup.academy.caio.TipoChave
import br.com.zup.academy.caio.TipoConta
import br.com.zup.academy.caio.chavepix.ChavePixRepository
import br.com.zup.academy.caio.chavepix.cria.ChavePix
import br.com.zup.academy.caio.externo.bcb.ChavePixBCBExterno
import br.com.zup.academy.caio.externo.bcb.delete.DeletePixKeyRequest
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class ExcluirChaveControllerTest(
    @Inject val repository: ChavePixRepository,
    @Inject val client: ExcluiChaveServiceGrpc.ExcluiChaveServiceBlockingStub){

    private lateinit var pix: ChavePix

    @Inject
    private lateinit var externoBCB: ChavePixBCBExterno

    @BeforeEach
    fun setup(){
        val chave = ChavePix(TipoChave.CPF, "12345678910", TipoConta.CONTA_CORRENTE, "280", "12345",
        "Joao da Silva", "12345678910", "12345678")
        pix = repository.save(chave)
    }

    @AfterEach
    internal fun tearDown() {
        repository.deleteAll()
    }

    @Test
    fun `deve excluir uma chave pix`() {
        //Cenario
        val request = ExclusaoChaveRequest.newBuilder()
            .setClienteId(pix.clienteId)
            .setPixId(pix.pixId)
            .build()

        val detelePixKeyRequest = DeletePixKeyRequest(pix.valor, "60701190")
        `when`(externoBCB.deletarChave(pix.valor, detelePixKeyRequest))
            .thenReturn(HttpResponse.ok())

        //Acao
        client.excluirChave(request)

        //Verificacao
        assertTrue(repository.findAll().isEmpty())

    }

    @Test
    fun `nao deve remover chave pix quando inexistente`(){
        //Cenario
        val request = ExclusaoChaveRequest.newBuilder()
            .setClienteId(pix.clienteId)
            .setPixId("17079741017")
            .build()
        //Acao
        val error = assertThrows<StatusRuntimeException> {
            client.excluirChave(request)
        }

        //Verificacao
        assertEquals(Status.NOT_FOUND.code, error.status.code)
        assertEquals("Chave nao encontrada ou nao pertencente ao cliente", error.status.description)
        assertFalse(repository.findAll().isEmpty())
    }

    @Test
    fun `nao deve remover chave pix não pertencente ao cliente`(){
        //Cenario
        val request = ExclusaoChaveRequest.newBuilder()
            .setClienteId("987654321")
            .setPixId(pix.pixId)
            .build()

        //Acao
        val error = assertThrows<StatusRuntimeException> {
            client.excluirChave(request)
        }

        //Verificacao
        assertEquals(Status.NOT_FOUND.code, error.status.code)
        assertEquals("Chave nao encontrada ou nao pertencente ao cliente", error.status.description)
        assertFalse(repository.findAll().isEmpty())
    }

    @Test
    fun `deve retornar excecao para chave nao encontrada pelo bacen`(){
        //Cenario
        val request = ExclusaoChaveRequest.newBuilder()
            .setClienteId(pix.clienteId)
            .setPixId(pix.pixId)
            .build()

        val deletePixKeyRequest = DeletePixKeyRequest(pix.valor, "60701190")
        `when`(externoBCB.deletarChave(pix.valor, deletePixKeyRequest))
            .thenReturn(HttpResponse.notFound())

        //Acao
        val error = assertThrows<StatusRuntimeException> {
            client.excluirChave(request)
        }

        //Verificacao
        with(error){
            assertEquals(Status.FAILED_PRECONDITION.code, error.status.code)
            assertEquals("Chave nao encontrada ou nao pertencente ao cliente", error.status.description)
            assertFalse(repository.findAll().isEmpty())
        }
    }

    @MockBean(ChavePixBCBExterno::class)
    fun mockChavePixBCBExterno(): ChavePixBCBExterno? {
        return Mockito.mock(ChavePixBCBExterno::class.java)
    }
}

@Factory
class excluirChaveClient(){

    @Singleton
    fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): ExcluiChaveServiceGrpc.ExcluiChaveServiceBlockingStub? {

        return ExcluiChaveServiceGrpc.newBlockingStub(channel)
    }

}


