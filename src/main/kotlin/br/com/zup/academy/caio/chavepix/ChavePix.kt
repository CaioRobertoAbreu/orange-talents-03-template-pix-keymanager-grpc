package br.com.zup.academy.caio.chavepix

import br.com.zup.academy.caio.TipoChave
import br.com.zup.academy.caio.TipoConta
import br.com.zup.academy.caio.externo.ConsultaCorrentistaResponse
import java.util.*
import javax.persistence.*

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
    val id_cliente: String,
) {

    init {
        if (tipoChave == TipoChave.CHAVE_ALEATORIA && valor.isNullOrBlank()){
            valor = UUID.randomUUID().toString()
        }
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var pixId: UUID? = null
}
