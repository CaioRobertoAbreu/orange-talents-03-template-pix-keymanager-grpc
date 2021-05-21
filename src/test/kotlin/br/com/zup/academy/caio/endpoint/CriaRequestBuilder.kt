package br.com.zup.academy.caio.endpoint

import br.com.zup.academy.caio.CadastraChaveRequest
import br.com.zup.academy.caio.TipoChave
import br.com.zup.academy.caio.TipoConta

class CriaRequestBuilder {

    private var codigoInterno: String = "5260263c-a3c1-4727-ae32-3bdb2538841b"
    private var tipoChave: Int = TipoChave.CPF_VALUE
    private var valor: String = "12345678910"
    private var tipoConta: Int = TipoConta.CONTA_CORRENTE_VALUE


    fun now(): CadastraChaveRequest{
        return CadastraChaveRequest.newBuilder()
            .setCodigoInterno(this.codigoInterno)
            .setTipoChaveValue(this.tipoChave)
            .setValor(this.valor)
            .setTipoContaValue(this.tipoConta)
            .build()
    }

    fun alterarConta(tipoConta: Int): CriaRequestBuilder {
        this.tipoConta = tipoConta
        return this
    }

    fun comCamposObrigatoriosInvalidos(): CriaRequestBuilder {
        this.codigoInterno = ""
        this.tipoChave = 0 //Valor padrão do GRPC
        this.valor = ""
        this.tipoConta = 0
        return this
    }

    fun comTipoDeChave(tipoChave: Int): CriaRequestBuilder {
        this.tipoChave = tipoChave
        return this
    }

    fun comValorChave(valor: String): CriaRequestBuilder {
        this.valor = valor
        return this
    }

}

