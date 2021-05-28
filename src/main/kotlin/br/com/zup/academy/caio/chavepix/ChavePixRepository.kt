package br.com.zup.academy.caio.chavepix

import br.com.zup.academy.caio.chavepix.cria.ChavePix
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository: JpaRepository<ChavePix, String> {

    fun existsByValor(valor: String): Boolean

    fun findByValor(valor: String): Optional<ChavePix>

    fun findByPixIdAndClienteId(pixId: String, clienteId: String): Optional<ChavePix>

    fun findAllByClienteId(clienteId: String): MutableIterable<ChavePix>
}