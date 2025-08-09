package com.example.petpal.domain.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.petpal.domain.model.PetRemote
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.Date
import java.util.concurrent.TimeUnit

class PetRepository(private val db: FirebaseFirestore) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Sử dụng địa chỉ IP của localhost trong giả lập Android
    private val localApiEndpoint = "http://10.0.2.2:3004/upload"

    fun fetchLostPets(onResult: (List<PetRemote>) -> Unit) {
        db.collection("lost_pets")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val pets = result.documents.mapNotNull { doc ->
                    try {
                        PetRemote(
                            id = doc.id,
                            petName = doc.getString("petName") ?: "",
                            breed = doc.getString("breed") ?: "",
                            color = doc.getString("color") ?: "",
                            features = doc.getString("features") ?: "",
                            personality = doc.getString("personality") ?: "",
                            circumstances = doc.getString("circumstances") ?: "",
                            accessories = doc.getString("accessories") ?: "",
                            contact = doc.getString("contact") ?: "",
                            location = doc.getString("location") ?: "",
                            imageUrls = doc.get("imageUrls") as? List<String> ?: emptyList(),
                            timestamp = doc.getTimestamp("timestamp")?.toDate()
                        )
                    } catch (e: Exception) {
                        Log.e("PetRepository", "Error mapping lost pet document: ${doc.id}", e)
                        null
                    }
                }
                onResult(pets)
            }
            .addOnFailureListener { e ->
                Log.e("PetRepository", "Error fetching lost pets", e)
                onResult(emptyList())
            }
    }

    fun fetchFoundPets(onResult: (List<PetRemote>) -> Unit) {
        db.collection("found_pets")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val pets = result.documents.mapNotNull { doc ->
                    try {
                        PetRemote(
                            id = doc.id,
                            petName = doc.getString("petName") ?: "",
                            breed = doc.getString("breed") ?: "",
                            color = doc.getString("color") ?: "",
                            features = doc.getString("features") ?: "",
                            personality = doc.getString("personality") ?: "",
                            circumstances = doc.getString("circumstances") ?: "",
                            accessories = doc.getString("accessories") ?: "",
                            contact = doc.getString("contact") ?: "",
                            location = doc.getString("location") ?: "",
                            imageUrls = doc.get("imageUrls") as? List<String> ?: emptyList(),
                            timestamp = doc.getTimestamp("timestamp")?.toDate()
                        )
                    } catch (e: Exception) {
                        Log.e("PetRepository", "Error mapping found pet document: ${doc.id}", e)
                        null
                    }
                }
                onResult(pets)
            }
            .addOnFailureListener { e ->
                Log.e("PetRepository", "Error fetching found pets", e)
                onResult(emptyList())
            }
    }

    fun addLostPet(
        pet: PetRemote,
        onSuccess: (DocumentReference) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val petWithTimestamp = pet.copy(timestamp = Date())
        db.collection("lost_pets")
            .add(petWithTimestamp)
            .addOnSuccessListener { onSuccess(it) }
            .addOnFailureListener { onFailure(it) }
    }

    fun addFoundPet(
        pet: PetRemote,
        onSuccess: (DocumentReference) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val petWithTimestamp = pet.copy(timestamp = Date())
        db.collection("found_pets")
            .add(petWithTimestamp)
            .addOnSuccessListener { onSuccess(it) }
            .addOnFailureListener { onFailure(it) }
    }

    fun uploadImages(
        context: Context,
        imageUris: List<Uri>,
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (imageUris.isEmpty()) {
            onSuccess(emptyList())
            return
        }

        val imageUrls = mutableListOf<String>()
        var uploadCount = 0

        imageUris.forEachIndexed { index, uri ->
            val file = getFileFromUri(context, uri)
            if (file == null) {
                onFailure(IOException("Could not get file from Uri: $uri"))
                return@forEachIndexed
            }
            val mimeType =
                getMimeType(context, uri)?.toMediaTypeOrNull() ?: "image/*".toMediaTypeOrNull()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "image", // Tên trường file mà API của bạn mong đợi
                    file.name,
                    file.asRequestBody(mimeType)
                )
                .build()

            val request = Request.Builder()
                .url(localApiEndpoint)
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onFailure(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        try {
                            val jsonObject = JSONObject(responseBody)
                            val imageUrl = jsonObject.getString("imageUrl")
                            imageUrls.add(imageUrl)
                            uploadCount++
                            if (uploadCount == imageUris.size) {
                                onSuccess(imageUrls)
                            }
                        } catch (e: Exception) {
                            onFailure(e)
                        }
                    } else {
                        onFailure(Exception("API call failed with code ${response.code}"))
                    }
                }
            })
        }
    }

    fun updatePetImageUrls(
        collectionPath: String,
        documentId: String,
        imageUrls: List<String>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(collectionPath).document(documentId)
            .update("imageUrls", imageUrls)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    private fun getMimeType(context: Context, uri: Uri): String? {
        return context.contentResolver.getType(uri)
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File(context.cacheDir, "temp_image")
        inputStream.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }

    // ✅ THAY THẾ HOÀN TOÀN HÀM CŨ
    suspend fun getPetById(petId: String): PetRemote? {
        // Bước 1: Thử tìm trong collection "lost_pets"
        try {
            val lostPetDoc = db.collection("lost_pets").document(petId).get().await()
            if (lostPetDoc.exists()) {
                // Nếu tìm thấy, chuyển đổi sang PetRemote và trả về
                return lostPetDoc.toObject(PetRemote::class.java)?.copy(id = lostPetDoc.id)
            }
        } catch (e: Exception) {
            Log.e("PetRepository", "Error fetching from lost_pets", e)
        }

        // Bước 2: Nếu không tìm thấy ở trên, thử tìm trong "found_pets"
        try {
            val foundPetDoc = db.collection("found_pets").document(petId).get().await()
            if (foundPetDoc.exists()) {
                // Nếu tìm thấy, chuyển đổi sang PetRemote và trả về
                return foundPetDoc.toObject(PetRemote::class.java)?.copy(id = foundPetDoc.id)
            }
        } catch (e: Exception) {
            Log.e("PetRepository", "Error fetching from found_pets", e)
        }

        // Bước 3: Nếu không tìm thấy ở cả hai nơi, trả về null
        return null
    }

    suspend fun getAllPets(): List<PetRemote> {
        return try {
            coroutineScope {
                val lostPetsDeferred = async { db.collection("lost_pets").get().await() }
                val foundPetsDeferred = async { db.collection("found_pets").get().await() }

                val lostPetsSnapshot = lostPetsDeferred.await()
                val foundPetsSnapshot = foundPetsDeferred.await()

                // ✅ SỬA LOGIC Ở ĐÂY: Lặp qua từng document để lấy ID
                val lostPets = lostPetsSnapshot.documents.mapNotNull { doc ->
                    // Chuyển đổi document sang object và copy ID vào
                    doc.toObject(PetRemote::class.java)?.copy(id = doc.id)
                }
                val foundPets = foundPetsSnapshot.documents.mapNotNull { doc ->
                    // Chuyển đổi document sang object và copy ID vào
                    doc.toObject(PetRemote::class.java)?.copy(id = doc.id)
                }

                // Gộp hai danh sách lại và trả về
                lostPets + foundPets
            }
        } catch (e: Exception) {
            Log.e("PetRepository", "Error fetching all pets", e)
            emptyList()
        }
    }
}