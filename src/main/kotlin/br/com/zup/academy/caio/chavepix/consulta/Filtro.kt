package br.com.zup.academy.caio.chavepix.consulta

import br.com.zup.academy.caio.chavepix.ChavePixRepository
import br.com.zup.academy.caio.exceptions.ChavePixNotFound
import br.com.zup.academy.caio.externo.bcb.ChavePixBCBExterno
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
@Validated
sealed class Filtro {

    abstract fun filtra(repository: ChavePixRepository, clientBCB: ChavePixBCBExterno): ConsultaChave

    @Introspected
    data class clienteId(@field:NotBlank @Size(max = 77) val pixId: String, @field:NotBlank val clienteId: String) : Filtro() {

        override fun filtra(repository: ChavePixRepository, clientBCB: ChavePixBCBExterno): ConsultaChave {

            repository.findByPixIdAndClienteId(pixId, clienteId).run {
                if (this.isEmpty) {
                    throw ChavePixNotFound("chave nao encontrada ou nao pertencente ao cliente")
                }

                clientBCB.consultarChave(this.get().valor).run {
                    if (this.status.code != HttpStatus.OK.code) {
                        throw ChavePixNotFound("chave nao encontrada ou nao pertencente ao cliente")
                    }
                }

                return ConsultaChave(this.get())
            }
        }
    }

    @Introspected
    data class chave(@field:NotBlank val chave: String) : Filtro() {

        override fun filtra(repository: ChavePixRepository, clientBCB: ChavePixBCBExterno): ConsultaChave {

            repository.findByValor(chave).run {
                if (this.isEmpty) {
                    clientBCB.consultarChave(chave).let {
                        if (it.status.code != HttpStatus.OK.code) {
                            throw ChavePixNotFound("chave nao encontrada ou nao pertencente ao cliente bcb")
                        }
                        return ConsultaChave(it.body()!!)
                    }
                }
                return ConsultaChave(this.get())
            }
        }

    }

    @Introspected
    class Invalido() : Filtro() {

        override fun filtra(repository: ChavePixRepository, clientBCB: ChavePixBCBExterno): ConsultaChave {
            throw IllegalStateException("Filtro invalido")
        }
    }

}