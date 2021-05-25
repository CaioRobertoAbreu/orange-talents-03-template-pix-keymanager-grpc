package br.com.zup.academy.caio.chavepix.cria

import br.com.zup.academy.caio.chavepix.ChavePixRepository
import br.com.zup.academy.caio.exceptions.ChavePixAlreadyExists
import br.com.zup.academy.caio.externo.bcb.ChavePixBCB
import br.com.zup.academy.caio.externo.erp_itau.ConsultaCorrentista
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class CriaChavePixService() {

    @Inject
    private lateinit var chavePixRepository: ChavePixRepository
    @Inject
    private lateinit var clientItau: ConsultaCorrentista
    @Inject
    private lateinit var clientBCB: ChavePixBCB
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun registra(@Valid novaChavePix: NovaChavePix): ChavePix {

        if (chavePixRepository.existsByValor(novaChavePix.valor)) {
            throw ChavePixAlreadyExists("Chave já cadastrada")
        }

        logger.info("Consultando chave ERP")
        val chavePix = clientItau.consultaCliente(novaChavePix).run{
            val chaveSalva = chavePixRepository.save(this)
            clientBCB.criaChaveBCB(chaveSalva)
            chavePixRepository.update(chaveSalva)
        }
        logger.info("Chave salva com sucesso!")

        return chavePix
    }


}