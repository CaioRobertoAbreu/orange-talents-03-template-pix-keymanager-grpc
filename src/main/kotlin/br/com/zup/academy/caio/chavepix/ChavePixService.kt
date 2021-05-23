package br.com.zup.academy.caio.chavepix

import br.com.zup.academy.caio.exceptions.ChavePixAlreadyExists
import br.com.zup.academy.caio.exceptions.ChavePixNotFound
import br.com.zup.academy.caio.externo.ConsultaCorrentista
import io.grpc.Status
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Valid

@Validated
@Singleton
class ChavePixService() {

    @Inject
    private lateinit var chavePixRepository: ChavePixRepository

    @Inject
    private lateinit var client: ConsultaCorrentista
    private val LOGGER = LoggerFactory.getLogger(this::class.java)


    fun registra(@Valid novaChavePix: NovaChavePix): ChavePix {

        if (chavePixRepository.existsByValor(novaChavePix.valor)) {
            throw ChavePixAlreadyExists("Chave já cadastrada")
        }

        val chavePix = client.consultaCliente(novaChavePix)

        return chavePixRepository.save(chavePix)
    }

    fun excluirChave(@Valid chave: ExcluirChave) {

        with(chave) {
            val chaveEncontrada = chavePixRepository.findById(chave.pixId)
            if (chaveEncontrada.isEmpty){
                throw ChavePixNotFound("Chave não encontrada")
            }

            if(chaveEncontrada.get().id_cliente != this.clienteId){
                throw Status.PERMISSION_DENIED.asRuntimeException()
            }

            chavePixRepository.deleteById(this.pixId)
        }
    }
}