package br.com.zup.academy.caio.validacao.custom

import br.com.zup.academy.caio.TipoChave
import br.com.zup.academy.caio.validacao.ValidaRequisicao
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.ConstraintViolation
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*

@MustBeDocumented
@Target(FIELD, FUNCTION, CLASS)
@Retention(RUNTIME)
@Constraint(validatedBy = [ChavePixValidator::class])
annotation class ChavePix(
    val message:String = "Valor da chave inválido"
) {
}

@Singleton
class ChavePixValidator: ConstraintValidator<ChavePix, ValidaRequisicao>{
    override fun isValid(
        value: ValidaRequisicao,
        annotationMetadata: AnnotationValue<ChavePix>,
        context: ConstraintValidatorContext
    ): Boolean {

        when(value.tipoChave){

            TipoChave.CPF.name -> return value.valor.isNotBlank() && value.valor.matches("^[0-9]{11}$".toRegex())

            TipoChave.CELULAR.name -> return value.valor.isNotBlank() && value.valor.matches("\\+[1-9]2\\d{1,14}$".toRegex())

            TipoChave.EMAIL.name -> return value.valor.isNotBlank() &&
                    value.valor.matches("^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$".toRegex())

            TipoChave.CHAVE_ALEATORIA.name -> return value.valor.isNullOrBlank()

            else -> return false
        }

    }

}