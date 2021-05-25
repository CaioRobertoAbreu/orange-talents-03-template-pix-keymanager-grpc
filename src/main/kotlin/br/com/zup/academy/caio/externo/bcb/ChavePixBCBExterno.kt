package br.com.zup.academy.caio.externo.bcb

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client("http://localhost:8082/api/v1/pix/keys")
interface ChavePixBCBExterno {

    @Post
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun criarChave(@Body chaveCreatePixKey: CreatePixKeyRequest ): HttpResponse<ResponseCriaChaveBCB>

    @Delete("/{key}")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun deletarChave(@PathVariable key: String, @Body DeletePixKeyRequest: DeletePixKeyRequest): HttpResponse<DeletePixKeyResponse>
}

