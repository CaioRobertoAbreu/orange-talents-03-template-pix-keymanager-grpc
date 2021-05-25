package br.com.zup.academy.caio.validacao_customizada

import br.com.zup.academy.caio.TipoChave
import br.com.zup.academy.caio.chavepix.NovaChavePix
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*

@MustBeDocumented
@Target(FIELD, FUNCTION, CLASS)
@Retention(RUNTIME)
@Constraint(validatedBy = [ChavePixValidator::class])
annotation class ChavePix(
    val message:String = "Valor da chave invalido"
) {
}

@Singleton
class ChavePixValidator: ConstraintValidator<ChavePix, NovaChavePix>{
    override fun isValid(
        value: NovaChavePix,
        annotationMetadata: AnnotationValue<ChavePix>,
        context: ConstraintValidatorContext
    ): Boolean {

        when(value.tipoChave){

            TipoChave.CPF -> return value.valor.isNotBlank() && value.valor.matches("^[0-9]{11}$".toRegex())

            TipoChave.CELULAR -> return value.valor.isNotBlank() && value.valor.matches("\\+[1-9]\\d{1,14}$".toRegex())

            TipoChave.EMAIL -> return value.valor.isNotBlank() &&
                    value.valor.matches("^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$".toRegex())

            TipoChave.CHAVE_ALEATORIA -> return value.valor.isNullOrBlank()

            else -> return false
        }

    }

}