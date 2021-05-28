package br.com.zup.academy.caio.chavepix.consulta

import br.com.zup.academy.caio.ConsultaChaveResponse
import br.com.zup.academy.caio.chavepix.cria.ChavePix
import br.com.zup.academy.caio.externo.bcb.consulta.PixKeyDetailsResponse
import java.time.LocalDateTime

class ConsultaChave(
    val pixId: String,
    val clienteId: String,
    val tipoChave: String,
    val valor: String,
    val nome: String,
    val cpf: String,
    val instituicao: String = "Itau Unibanco",
    val agencia: String,
    val numero: String,
    val tipoConta: String,
    val criadoEm: LocalDateTime? = null
) {


    constructor(chave: ChavePix) : this(
        pixId = chave.pixId!!,
        clienteId = chave.clienteId,
        tipoChave = chave.tipoChave.name,
        valor = chave.valor,
        nome = chave.nome,
        cpf = chave.cpf,
        agencia = chave.agencia,
        numero = chave.numero,
        tipoConta = chave.tipoConta.name,
        criadoEm = chave.criadoEm
    ){ }

    constructor(chave: PixKeyDetailsResponse) : this(
        pixId ="",
        clienteId = "",
        tipoChave = PixKeyDetailsResponse.toTipoChave(chave.keyType),
        valor = chave.key,
        nome = chave.owner.name,
        cpf = chave.owner.taxIdNumber,
        agencia = chave.bankAccount.branch,
        numero = chave.bankAccount.accountNumber,
        tipoConta = PixKeyDetailsResponse.toTipoConta(chave.bankAccount.accountType),
        criadoEm = chave.createdAt
    ){ }

    fun converteToResponse(): ConsultaChaveResponse {

        return ConsultaChaveResponse.newBuilder()
            .setPixId(this.pixId)
            .setClienteId(this.clienteId)
            .setTipoChave(this.tipoChave)
            .setValor(this.valor)
            .setNome(this.nome)
            .setCpf(this.cpf)
            .setInstituicaoFinanceira(this.instituicao)
            .setAgencia(this.agencia)
            .setNumero(this.numero)
            .setTipoConta(this.tipoConta)
            .setCriadoEm(this.criadoEm.toString())
            .build()
    }


}