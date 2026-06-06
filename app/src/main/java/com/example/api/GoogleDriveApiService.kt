package com.example.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

data class DriveFileListResponse(
    val files: List<DriveFileResponse> = emptyList(),
    val nextPageToken: String? = null
)

data class DriveFileResponse(
    val id: String,
    val name: String,
    val mimeType: String,
    val thumbnailLink: String? = null,
    val size: String? = null,
    val modifiedTime: String? = null,
    val parents: List<String>? = null
)

data class CreateFolderRequest(
    val name: String,
    val mimeType: String = "application/vnd.google-apps.folder",
    val parents: List<String>? = null
)

interface GoogleDriveApiService {

    @GET("drive/v3/files")
    suspend fun listFiles(
        @Header("Authorization") authHeader: String,
        @Query("q") query: String,
        @Query("pageSize") pageSize: Int = 20,
        @Query("fields") fields: String = "files(id, name, mimeType, thumbnailLink, size, modifiedTime, parents), nextPageToken"
    ): DriveFileListResponse

    @POST("drive/v3/files")
    suspend fun createFolder(
        @Header("Authorization") authHeader: String,
        @Body request: CreateFolderRequest
    ): DriveFileResponse

    @POST("drive/v3/files")
    suspend fun createMetadataOnlyFile(
        @Header("Authorization") authHeader: String,
        @Body metadata: DriveFileResponse
    ): DriveFileResponse

    @Multipart
    @POST("upload/drive/v3/files")
    suspend fun uploadMultipartFile(
        @Header("Authorization") authHeader: String,
        @Query("uploadType") uploadType: String = "multipart",
        @retrofit2.http.Part metadata: MultipartBody.Part,
        @retrofit2.http.Part fileContent: MultipartBody.Part
    ): DriveFileResponse
}

object DriveRetrofitClient {
    private const val BASE_URL = "https://www.googleapis.com/"

    val service: GoogleDriveApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.moshi.MoshiConverterFactory.create())
            .build()
        retrofit.create(GoogleDriveApiService::class.java)
    }
}
