package br.com.zup.academy.caio.chavepix.deleta

import br.com.zup.academy.caio.chavepix.ChavePixRepository
import br.com.zup.academy.caio.exceptions.ChavePixNotFound
import br.com.zup.academy.caio.externo.bcb.ChavePixBCB
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class DeletaChavePixService {

    @Inject
    private lateinit var chavePixBCB: ChavePixBCB
    @Inject
    private lateinit var chavePixRepository: ChavePixRepository
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun excluirChave(@Valid chave: ExcluirChave) {

        val chaveEncontrada = chavePixRepository
            .findByPixIdAndClienteId(chave.pixId, chave.clienteId)
            .orElseThrow { ChavePixNotFound("Chave nao encontrada ou nao pertencente ao cliente") }



        with(chaveEncontrada) {
            val chave = chavePixBCB.deletarChaveBCB(this)

            chavePixRepository.deleteById(chave.pixId)
            logger.info("Chave excluida com sucesso!")
        }
    }

}