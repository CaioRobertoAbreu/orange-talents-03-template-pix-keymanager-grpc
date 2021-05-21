package br.com.zup.academy.caio.chavepix

import br.com.zup.academy.caio.exceptions.ChavePixAlreadyExists
import br.com.zup.academy.caio.externo.ConsultaCorrentista
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Valid

@Validated
@Singleton
class NovaChavePixService() {

    @Inject
    private lateinit var chavePixRepository: ChavePixRepository
    @Inject
    private lateinit var client: ConsultaCorrentista
    private val LOGGER = LoggerFactory.getLogger(this::class.java)


    fun registra(@Valid novaChavePix: NovaChavePix): ChavePix {

        if(chavePixRepository.existsByValor(novaChavePix.valor)){
            throw ChavePixAlreadyExists("Chave já cadastrada")
        }

        val chavePix = client.consultaCliente(novaChavePix)

        return chavePixRepository.save(chavePix)
    }
}