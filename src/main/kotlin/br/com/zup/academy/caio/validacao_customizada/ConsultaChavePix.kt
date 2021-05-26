package br.com.zup.academy.caio.validacao_customizada

import br.com.zup.academy.caio.chavepix.consulta.ConsultaChave
import io.micronaut.core.annotation.AnnotationValue
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext

@MustBeDocumented
@Retention(RUNTIME)
@Target(FIELD, FUNCTION, CLASS)
@Constraint(validatedBy = [ConsultaChavePixValidator::class])
annotation class ConsultaChavePix(
    val message: String = "Preenchimento inválido",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
) {
}

@Singleton
class ConsultaChavePixValidator : ConstraintValidator<ConsultaChavePix, ConsultaChave> {

    override fun isValid(
        value: ConsultaChave,
        annotationMetadata: AnnotationValue<ConsultaChavePix>,
        context: ConstraintValidatorContext): Boolean {

        if (!value.clienteId.isBlank() && !value.pixId.isEmpty() && value.chavePix.isEmpty()) {
            return true
        }

        if (!value.chavePix.isEmpty() && value.clienteId.isEmpty() && value.pixId.isEmpty()) {
            return true
        }

        context.messageTemplate("Informe os campos corretamente")
        return false

    }
}