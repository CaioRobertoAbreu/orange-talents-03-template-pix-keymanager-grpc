package br.com.zup.academy.caio.externo.bcb

import br.com.zup.academy.caio.externo.bcb.consulta.PixKeyDetailsResponse
import br.com.zup.academy.caio.externo.bcb.cria.CreatePixKeyRequest
import br.com.zup.academy.caio.externo.bcb.cria.CreatePixKeyResponse
import br.com.zup.academy.caio.externo.bcb.delete.DeletePixKeyRequest
import br.com.zup.academy.caio.externo.bcb.delete.DeletePixKeyResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client("http://localhost:8082/api/v1/pix/keys")
interface ChavePixBCBExterno {

    @Post
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun criarChave(@Body chaveCreatePixKey: CreatePixKeyRequest): HttpResponse<CreatePixKeyResponse>

    @Delete("/{key}")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun deletarChave(@PathVariable key: String, @Body DeletePixKeyRequest: DeletePixKeyRequest): HttpResponse<DeletePixKeyResponse>

    @Get("/{key}")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun consultarChave(@PathVariable key: String): HttpResponse<PixKeyDetailsResponse>
}

