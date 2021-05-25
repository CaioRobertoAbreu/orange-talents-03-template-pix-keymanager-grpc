package br.com.zup.academy.caio.chavepix

import br.com.zup.academy.caio.TipoChave
import br.com.zup.academy.caio.TipoConta
import java.util.*
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import kotlin.random.Random

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
) {
    @Id
    var pixId: String? = null

    init {
        if (tipoChave == TipoChave.CHAVE_ALEATORIA && valor.isNullOrBlank()){
            valor = UUID.randomUUID().toString()
        }
        pixId = UUID.randomUUID().toString()
    }

    fun atualiza(key: String){
        this.valor = key
    }

}
