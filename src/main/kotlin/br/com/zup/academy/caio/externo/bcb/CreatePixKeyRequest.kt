package br.com.zup.academy.caio.externo.bcb

data class CreatePixKeyRequest(
    val keyType: String,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner
) {

}
