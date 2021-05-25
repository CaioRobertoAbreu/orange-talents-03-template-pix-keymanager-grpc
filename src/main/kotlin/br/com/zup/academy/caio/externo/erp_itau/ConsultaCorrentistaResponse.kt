package br.com.zup.academy.caio.externo.erp_itau

import br.com.zup.academy.caio.TipoConta
import br.com.zup.academy.caio.chavepix.cria.ChavePix
import br.com.zup.academy.caio.chavepix.cria.NovaChavePix

data class ConsultaCorrentistaResponse(
    val tipo: String,
    val agencia: String,
    val numero: String,
    val titular: Titular
) {
    val titular_id = titular.id
    val nome = titular.nome
    val cpf = titular.cpf

    fun toChavePix(novaChavePix: NovaChavePix): ChavePix {
        return ChavePix(novaChavePix.tipoChave!!, novaChavePix.valor, TipoConta.valueOf(this.tipo),
            this.agencia, this.numero, this.nome, this.cpf, this.titular_id)
    }

}
data class Titular(
    val id: String,
    val nome: String,
    val cpf: String
)
