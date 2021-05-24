package br.com.zup.academy.caio.chavepix

import br.com.zup.academy.caio.ExcluiChaveServiceGrpc
import br.com.zup.academy.caio.ExclusaoChaveRequest
import br.com.zup.academy.caio.TipoChave
import br.com.zup.academy.caio.TipoConta
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.annotation.GrpcService
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class ExcluirChaveControllerTest(
    @Inject val repository: ChavePixRepository,
    @Inject val client: ExcluiChaveServiceGrpc.ExcluiChaveServiceBlockingStub){

    private lateinit var pix: ChavePix

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
        assertEquals("Chave não encontrada ou não pertencente ao cliente", error.status.description)
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
        assertEquals("Chave não encontrada ou não pertencente ao cliente", error.status.description)
        assertFalse(repository.findAll().isEmpty())
    }
}

@Factory
class excluirChaveClient(){

    @Singleton
    fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): ExcluiChaveServiceGrpc.ExcluiChaveServiceBlockingStub? {

        return ExcluiChaveServiceGrpc.newBlockingStub(channel)
    }

}


