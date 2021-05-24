package br.com.zup.academy.caio.chavepix

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository: JpaRepository<ChavePix, String> {

    fun existsByValor(valor: String): Boolean

    fun existsByPixIdAndClienteId(pixId: String, clienteId: String): Boolean
//    fun existsByPixIdAndId_Cliente(pixId: String, id_Cliente: String): Boolean

}