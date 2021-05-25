package br.com.zup.academy.caio.externo.erp_itau

import br.com.zup.academy.caio.chavepix.ChavePix
import br.com.zup.academy.caio.chavepix.NovaChavePix
import javax.inject.Singleton

@Singleton
class ConsultaCorrentista(
    val clientItau: ConsultaCorrentistaClient) {

    fun consultaCliente(novaChavePix: NovaChavePix): ChavePix {

        return clientItau.consultaCliente(novaChavePix.codigoInterno, novaChavePix.tipoConta!!.name)
            .run {
                if(this == null){
                    throw IllegalArgumentException("Dados inválidos")
                }
                this.body().toChavePix(novaChavePix)
            }
    }
}
