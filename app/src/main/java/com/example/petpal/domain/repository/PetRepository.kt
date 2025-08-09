package com.example.petpal.domain.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.petpal.domain.model.PetRemote
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.Date
import java.util.concurrent.TimeUnit

class PetRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

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

    fun addLostPet(pet: PetRemote, onSuccess: (DocumentReference) -> Unit, onFailure: (Exception) -> Unit) {
        val petWithTimestamp = pet.copy(timestamp = Date())
        db.collection("lost_pets")
            .add(petWithTimestamp)
            .addOnSuccessListener { onSuccess(it) }
            .addOnFailureListener { onFailure(it) }
    }

    fun addFoundPet(pet: PetRemote, onSuccess: (DocumentReference) -> Unit, onFailure: (Exception) -> Unit) {
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
            val mimeType = getMimeType(context, uri)?.toMediaTypeOrNull() ?: "image/*".toMediaTypeOrNull()

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
}