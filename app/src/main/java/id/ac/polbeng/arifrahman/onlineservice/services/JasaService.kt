package id.ac.polbeng.arifrahman.onlineservice.services

import id.ac.polbeng.arifrahman.onlineservice.models.DefaultResponse
import id.ac.polbeng.arifrahman.onlineservice.models.JasaResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface JasaService {

    @GET("services")
    fun getJasa(): Call<JasaResponse>

    @GET("userServices/{id}")
    fun getJasaUser(
        @Path("id") id: Int
    ): Call<JasaResponse>

    @Multipart
    @POST("services")
    fun addJasa(
        @Part image: MultipartBody.Part,
        @Part("id_user") idUser: RequestBody,
        @Part("nama_jasa") namaJasa: RequestBody,
        @Part("deskripsi_singkat") deskripsiSingkat: RequestBody,
        @Part("uraian_deskripsi") uraianDeskripsi: RequestBody,
        @Part("rating") rating: RequestBody
    ): Call<DefaultResponse>

    @Multipart
    @PUT("services")
    fun editJasaReplaceImage(
        @Part image: MultipartBody.Part,
        @Part("id") idJasa: RequestBody,
        @Part("nama_jasa") namaJasa: RequestBody,
        @Part("deskripsi_singkat") deskripsiSingkat: RequestBody,
        @Part("uraian_deskripsi") uraianDeskripsi: RequestBody,
        @Part("file") gambar: RequestBody
    ) : Call<DefaultResponse>

    @Multipart
    @PUT("services")
    fun editJasaReplaceImage(
        @Part image: MultipartBody.Part,
        @Part("id") idJasa: RequestBody,
        @Part("nama_jasa") namaJasa: RequestBody,
        @Part("deskripsi_singkat") deskripsiSingkat: RequestBody,
        @Part("uraian_deskripsi") uraianDeskripsi: RequestBody
    ): Call<DefaultResponse>

    @DELETE("services/{id}")
    fun deleteService(
        @Path("id") idJasa: Int
    ) : Call<DefaultResponse>
}
