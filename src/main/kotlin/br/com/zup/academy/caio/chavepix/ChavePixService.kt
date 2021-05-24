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
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class ChavePixService() {

    @Inject
    private lateinit var chavePixRepository: ChavePixRepository

    @Inject
    private lateinit var client: ConsultaCorrentista
    private val LOGGER = LoggerFactory.getLogger(this::class.java)


    @Transactional
    fun registra(@Valid novaChavePix: NovaChavePix): ChavePix {

        if (chavePixRepository.existsByValor(novaChavePix.valor)) {
            throw ChavePixAlreadyExists("Chave já cadastrada")
        }

        val chavePix = client.consultaCliente(novaChavePix)

        return chavePixRepository.save(chavePix)
    }

    @Transactional
    fun excluirChave(@Valid chave: ExcluirChave) {

        with(chave) {

            if(chavePixRepository.existsByPixIdAndClienteId(chave.pixId, chave.clienteId)){
                chavePixRepository.deleteById(this.pixId)
            }else {
                throw ChavePixNotFound("Chave não encontrada ou não pertencente ao cliente")
            }

        }
    }
}