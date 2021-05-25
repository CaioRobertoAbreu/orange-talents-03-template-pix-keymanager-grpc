package br.com.zup.academy.caio.externo.bcb.cria

data class CreatePixKeyRequest(
    val keyType: String,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner
) {

}

data class BankAccount(
    val participant: String, //ipsb
    val branch: String,
    var accountNumber: String,
    val accountType: String
) {}

data class Owner(
    val type: String,
    val name: String,
    val taxIdNumber: String //CPF
) {}

enum class OwnerType(val descricao: String) {

    NATURAL_PERSON("Pessoa Física"),
    LEGAL_PERSON("Pessoa Jurídica")
}
