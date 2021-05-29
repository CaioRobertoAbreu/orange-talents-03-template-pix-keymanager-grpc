package br.com.zup.academy.caio.validacao_customizada

import br.com.zup.academy.caio.TipoChave
import br.com.zup.academy.caio.chavepix.cria.NovaChavePix
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

@MustBeDocumented
@Target(FIELD, FUNCTION, CLASS)
@Retention(RUNTIME)
@Constraint(validatedBy = [ChavePixValidator::class])
annotation class ChavePix(
    val message: String = "Valor da chave invalido",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<out Payload>> = []
) {
}

@Singleton
class ChavePixValidator : ConstraintValidator<ChavePix, NovaChavePix> {

    override fun isValid(value: NovaChavePix, context: ConstraintValidatorContext): Boolean {

        when (value.tipoChave) {
            TipoChave.CPF -> {
                return value.valor.isNotBlank() && value.valor.matches("^[0-9]{11}$".toRegex()).also {
                    if (!it) {
                        addConstraint(context)
                    }
                }
            }

            TipoChave.CELULAR -> {
                return value.valor.isNotBlank() && value.valor.matches("\\+[1-9]\\d{1,14}$".toRegex()).also {
                    if (!it) {
                        addConstraint(context)
                    }
                }
            }

            TipoChave.EMAIL -> {
                return value.valor.isNotBlank() &&
                        value.valor.matches("^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$".toRegex())
                            .also {
                                if (!it) {
                                    addConstraint(context)
                                }
                            }
            }

            TipoChave.CHAVE_ALEATORIA -> {
                return value.valor.isBlank().also {
                    if (!it) {
                        addConstraint(context)
                    }
                }
            }

            TipoChave.CHAVE_DESCONHECIDA -> {
                addConstraint(context)
                return false
            }

            else -> return false
        }
    }

    fun addConstraint(context: ConstraintValidatorContext) {
        context.disableDefaultConstraintViolation()
        context.buildConstraintViolationWithTemplate("tipoChave invalido")
            .addPropertyNode("tipoChave")
            .addConstraintViolation()
    }

}