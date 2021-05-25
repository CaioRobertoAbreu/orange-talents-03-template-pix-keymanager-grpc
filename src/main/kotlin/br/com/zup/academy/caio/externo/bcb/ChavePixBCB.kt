package br.com.zup.academy.caio.externo.bcb

import br.com.zup.academy.caio.TipoChave
import br.com.zup.academy.caio.chavepix.cria.ChavePix
import br.com.zup.academy.caio.externo.bcb.*
import br.com.zup.academy.caio.externo.bcb.cria.*
import br.com.zup.academy.caio.externo.bcb.delete.DeletePixKeyRequest
import io.micronaut.http.HttpStatus
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class ChavePixBCB(val clientBCBExterno: ChavePixBCBExterno) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun criaChaveBCB(chave: ChavePix): ChavePix {

        val bankAccount =
            BankAccount("60701190", chave.agencia, chave.numero, AccountType.converte(chave.tipoConta.name))
        val owner = Owner(OwnerType.NATURAL_PERSON.name, chave.nome, chave.cpf)
        val request = CreatePixKeyRequest(chave.tipoChave.converte(), chave.valor, bankAccount, owner)

        logger.info("Salvando chave no BCB")
        val response = clientBCBExterno.criarChave(request).run {
            if (this.status.code != HttpStatus.CREATED.code) {
                throw IllegalStateException("Erro ao cadastrar chave no Bacen")
            }
            this.body.get()
        }

        chave.atualiza(response.key)

        return chave
    }

    fun deletarChaveBCB(chave: ChavePix): ChavePix {

        val request = DeletePixKeyRequest(chave.valor, "60701190")

        logger.info("Deletando chave do BCB")
        clientBCBExterno.deletarChave(chave.valor, request).run {
            if (this.status.code != HttpStatus.OK.code) {
                throw IllegalStateException("Chave nao encontrada ou nao pertencente ao cliente")
            }
        }

        return chave

    }
}

fun TipoChave.converte(): String {

    if (this.name == TipoChave.EMAIL.name) {
        return this.name
    }

    if (this.name == TipoChave.CELULAR.name) {
        return "PHONE"
    }

    if (this.name == TipoChave.CPF.name) {
        return this.name
    }

    if (this.name == TipoChave.CHAVE_ALEATORIA.name) {
        return "RANDOM"
    }

    return ""
}