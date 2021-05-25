package br.com.zup.academy.caio.externo.bcb

data class BankAccount(
    val participant: String, //ipsb
    val branch: String,
    var accountNumber: String,
    val accountType: String
) {

}
