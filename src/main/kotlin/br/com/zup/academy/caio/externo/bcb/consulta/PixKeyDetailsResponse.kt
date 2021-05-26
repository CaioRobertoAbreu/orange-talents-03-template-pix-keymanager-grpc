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

    fun toChavePix(): ChavePix {

       return ChavePix(converteToTipoChave(), this.key, convertoToTipoConta(), bankAccount.branch,
        bankAccount.accountNumber, owner.name, owner.taxIdNumber, "", this.createdAt).also {
            it.pixId = ""
        }

    }

    private fun converteToTipoChave(): TipoChave{
        return when(keyType){
            "cpf".toUpperCase() -> return TipoChave.CPF
            "phone".toUpperCase() -> return TipoChave.CELULAR
            "email".toUpperCase() -> return TipoChave.EMAIL
            "random".toUpperCase() -> return TipoChave.CHAVE_ALEATORIA
            else -> TipoChave.CHAVE_DESCONHECIDA
        }
    }

    private fun convertoToTipoConta(): TipoConta{
        return when(bankAccount.accountType){
            "cacc".toUpperCase() -> return TipoConta.CONTA_CORRENTE
            "svgs ".toUpperCase() -> return TipoConta.CONTA_POUPANCA
            else -> TipoConta.CONTA_DESCONHECIDA
        }
    }
}
