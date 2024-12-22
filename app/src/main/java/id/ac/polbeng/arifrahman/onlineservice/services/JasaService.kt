package id.ac.polbeng.arifrahman.onlineservice.services

import id.ac.polbeng.arifrahman.onlineservice.models.JasaResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
interface JasaService {
    @GET("services")
    fun getJasa() : Call<JasaResponse>

    @GET("userServices/{id}")
    fun getJasaUser(
        @Path("id") id: Int
    ) : Call<JasaResponse>
}