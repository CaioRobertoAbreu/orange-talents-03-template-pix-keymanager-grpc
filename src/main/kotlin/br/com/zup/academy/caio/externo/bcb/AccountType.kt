package br.com.zup.academy.caio.externo.bcb

import br.com.zup.academy.caio.TipoConta

enum class AccountType(val descricao: String) {

    CACC("Conta Corrente"),
    SVGS("Conta Poupança");

    companion object {

        fun converte(valor: String): String {
            if(valor.equals(TipoConta.CONTA_CORRENTE.name)){
                return CACC.name
            }

            if(valor.equals(TipoConta.CONTA_POUPANCA.name)){
                return SVGS.name
            }

            throw IllegalArgumentException("Tipo de conta inválida")
        }

    }


}


