package br.com.zup.academy.caio.externo.bcb.consulta

import br.com.zup.academy.caio.TipoChave
import br.com.zup.academy.caio.TipoConta
import br.com.zup.academy.caio.chavepix.cria.ChavePix
import br.com.zup.academy.caio.externo.bcb.cria.BankAccount
import br.com.zup.academy.caio.externo.bcb.cria.Owner
import java.time.LocalDateTime

class PixKeyDetailsResponse(
    val keyType: String,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime

) {

    companion object converte {

        fun toTipoChave(chave: String): String{
            return when(chave){
                "cpf".toUpperCase() -> return TipoChave.CPF.name
                "phone".toUpperCase() -> return TipoChave.CELULAR.name
                "email".toUpperCase() -> return TipoChave.EMAIL.name
                "random".toUpperCase() -> return TipoChave.CHAVE_ALEATORIA.name
                else -> TipoChave.CHAVE_DESCONHECIDA.name
            }
        }

        fun toTipoConta(type: String): String{
            return when(type){
                "cacc".toUpperCase() -> return TipoConta.CONTA_CORRENTE.name
                "svgs ".toUpperCase() -> return TipoConta.CONTA_POUPANCA.name
                else -> TipoConta.CONTA_DESCONHECIDA.name
            }
        }

    }


}
