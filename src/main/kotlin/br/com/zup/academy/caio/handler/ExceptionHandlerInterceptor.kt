package br.com.zup.academy.caio.handler

import br.com.zup.academy.caio.chavepix.CriaChaveController
import br.com.zup.academy.caio.exceptions.ChavePixAlreadyExists
import br.com.zup.academy.caio.exceptions.ChavePixNotFound
import com.google.rpc.BadRequest
import com.google.rpc.Code
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import io.micronaut.http.client.exceptions.HttpClientResponseException
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@InterceptorBean(ErrorHandler::class)
class ExceptionHandlerInterceptor: MethodInterceptor<CriaChaveController, Any?> {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    override fun intercept(context: MethodInvocationContext<CriaChaveController, Any?>): Any? {

        LOGGER.info("Interceptando metodo ${context.targetMethod}")

        try {
            return context.proceed() //Processa método interceptado
        }catch (e: Exception){

            val error = when (e) {
                is IllegalArgumentException -> Status.INVALID_ARGUMENT.withDescription(e.message).asRuntimeException()
                is HttpClientResponseException -> Status.INVALID_ARGUMENT.withDescription("Dados inválidos").asRuntimeException()
                is IllegalStateException -> Status.FAILED_PRECONDITION.withDescription(e.message).asRuntimeException()
                is ChavePixAlreadyExists -> Status.ALREADY_EXISTS.withDescription(e.message).asRuntimeException()
                is ConstraintViolationException -> constraintViolationException(e)
                is ChavePixNotFound -> Status.NOT_FOUND.withDescription(e.message).asRuntimeException()
                else -> Status.UNKNOWN.withDescription("Erro inesperado").asRuntimeException()
            }

            val responseObserver = context.parameterValues[1] as StreamObserver<*>
            responseObserver.onError(error)

            return null
        }
    }

    private fun constraintViolationException(e: ConstraintViolationException): StatusRuntimeException{

        val constraints: List<BadRequest.FieldViolation> = e.constraintViolations.map {
                            BadRequest.FieldViolation.newBuilder()
                                .setField(it.propertyPath.last().name)
                                .setDescription(it.message)
                                .build()
                        }

        val details = BadRequest.newBuilder()
            .addAllFieldViolations(constraints)
            .build()


        val statusProto = com.google.rpc.Status.newBuilder()
            .setCode(Code.INVALID_ARGUMENT_VALUE)
            .setMessage("Parâmetros inválidos")
            .addDetails(com.google.protobuf.Any.pack(details))
            .build()

        LOGGER.info("StatusProto: $statusProto")

        return StatusProto.toStatusRuntimeException(statusProto)
    }

}