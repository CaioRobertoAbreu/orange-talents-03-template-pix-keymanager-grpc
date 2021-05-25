package br.com.zup.academy.caio.chavepix.cria

import br.com.zup.academy.caio.TipoChave
import br.com.zup.academy.caio.TipoConta
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class ChavePixTest{

    @Test
    fun `deve gerar chave aleatoria`(){
        //Cenario

        //Acao
        val chave = ChavePix(TipoChave.CHAVE_DESCONHECIDA, "", TipoConta.CONTA_CORRENTE, "7000", "1000",
            "Leonardo", "05656198022", "1248163264128")

        //Verificacao
        assertTrue(chave.pixId!!.isNotBlank())
        assertNotNull(chave.tipoChave)
        assertNotNull(chave.valor)
        assertNotNull(chave.tipoConta)
        assertNotNull(chave.agencia)
        assertNotNull(chave.numero)
        assertNotNull(chave.nome)
        assertNotNull(chave.cpf)
        assertNotNull(chave.clienteId)
    }
}