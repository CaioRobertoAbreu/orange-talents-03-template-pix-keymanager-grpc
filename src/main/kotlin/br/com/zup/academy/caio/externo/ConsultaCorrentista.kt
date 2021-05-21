package br.com.zup.academy.caio.externo

import br.com.zup.academy.caio.chavepix.ChavePix
import br.com.zup.academy.caio.chavepix.NovaChavePix
import javax.inject.Singleton

@Singleton
class ConsultaCorrentista(
    val client: ConsultaCorrentistaClient
) {

    fun consultaCliente(novaChavePix: NovaChavePix): ChavePix {

        client.consultaCliente(novaChavePix.codigoInterno, novaChavePix.tipoConta!!.name)
            .apply {
                if(this == null) {
                    throw IllegalArgumentException("Dados inválidos")
                } else {
                    return this.toChavePix(novaChavePix)
                }
            }

    }
}