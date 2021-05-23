package br.com.zup.academy.caio.exceptions

import javax.inject.Singleton

@Singleton
class ChavePixNotFound(message: String): RuntimeException(message) {

}