package br.com.zup.academy.caio.chavepix.cria

import br.com.zup.academy.caio.TipoChave
import br.com.zup.academy.caio.TipoConta
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity
class ChavePix(
    @Enumerated(EnumType.STRING)
    val tipoChave: TipoChave,
    var valor: String,
    @Enumerated(EnumType.STRING)
    val tipoConta: TipoConta,
    val agencia: String,
    val numero: String,
    val nome: String,
    val cpf: String,
    val clienteId: String,
    var criadoEm: LocalDateTime? = null
) {
    @Id
    var pixId: String? = null


    init {
        if (tipoChave == TipoChave.CHAVE_ALEATORIA && valor == null){
            valor = UUID.randomUUID().toString()
        }
        pixId = UUID.randomUUID().toString()
    }

    fun atualiza(key: String, criadoEm: LocalDateTime){
        this.valor = key
        this.criadoEm = criadoEm
    }

}
