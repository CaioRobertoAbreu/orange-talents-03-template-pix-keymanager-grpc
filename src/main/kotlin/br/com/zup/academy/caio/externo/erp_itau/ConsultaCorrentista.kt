package br.com.zup.academy.caio.externo.erp_itau

import br.com.zup.academy.caio.chavepix.cria.ChavePix
import br.com.zup.academy.caio.chavepix.cria.NovaChavePix
import javax.inject.Singleton

@Singleton
class ConsultaCorrentista(
    val externoItau: ConsultaCorrentistaExterno) {

    fun consultaCliente(novaChavePix: NovaChavePix): ChavePix {

        return externoItau.consultaCliente(novaChavePix.codigoInterno, novaChavePix.tipoConta!!.name)
            .run {
                if(this == null){
                    throw IllegalArgumentException("Dados inválidos")
                }
                this.body().toChavePix(novaChavePix)
            }
    }
}
