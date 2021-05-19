package br.com.zup.academy.caio.exceptions

import java.lang.RuntimeException
import javax.inject.Singleton

@Singleton
class ChavePixAlreadyExists(message: String): RuntimeException(message) {
}