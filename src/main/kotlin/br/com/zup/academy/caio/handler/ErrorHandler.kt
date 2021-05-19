package br.com.zup.academy.caio.handler

import io.micronaut.aop.Around
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION

@Around
@MustBeDocumented
@Target(FUNCTION, CLASS)
@Retention(RUNTIME)
annotation class ErrorHandler()
