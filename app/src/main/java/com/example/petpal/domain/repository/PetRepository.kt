package com.example.petpal.domain.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.petpal.domain.model.PetRemote
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.Date
import java.util.UUID

class PetRepository(private val db: FirebaseFirestore) {

    private val storage = FirebaseStorage.getInstance()


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
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFailure)
    }

    fun addFoundPet(
        pet: PetRemote,
        onSuccess: (DocumentReference) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val petWithTimestamp = pet.copy(timestamp = Date())
        db.collection("found_pets")
            .add(petWithTimestamp)
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFailure)
    }

    /**
     * Upload danh sách ảnh lên Firebase Storage và trả về list HTTPS URL.
     * @param folderId thư mục lưu ảnh (nên dùng documentId để gom theo từng pet)
     */
    suspend fun uploadImagesToFirebase(
        context: Context,
        folderId: String,
        imageUris: List<Uri>
    ): List<String> {
        if (imageUris.isEmpty()) return emptyList()

        val urls = mutableListOf<String>()
        for ((index, uri) in imageUris.withIndex()) {
            val fileName = "${index}_${UUID.randomUUID()}.jpg"
            val ref = storage.reference.child("pets/$folderId/$fileName")

            // putFile + đợi hoàn tất
            ref.putFile(uri).await()

            // lấy https download URL
            val url = ref.downloadUrl.await().toString()
            urls.add(url)
        }
        return urls
    }

    fun updatePetImageUrls(
        collectionPath: String,
        documentId: String,
        imageUrls: List<String>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(collectionPath).document(documentId)
            .update(
                mapOf(
                    "imageUrls" to imageUrls,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
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

    // ✅ COMPLETELY REPLACES THE OLD FUNCTION
    suspend fun getPetById(petId: String): PetRemote? {
        // Step 1: Try searching in the "lost_pets" collection
        try {
            val lostPetDoc = db.collection("lost_pets").document(petId).get().await()
            if (lostPetDoc.exists()) {
                // If found, convert to PetRemote and return
                return lostPetDoc.toObject(PetRemote::class.java)?.copy(id = lostPetDoc.id)
            }
        } catch (e: Exception) {
            Log.e("PetRepository", "Error fetching from lost_pets", e)
        }

        // Step 2: If not found above, try searching in "found_pets"
        try {
            val foundPetDoc = db.collection("found_pets").document(petId).get().await()
            if (foundPetDoc.exists()) {
                // If found, convert to PetRemote and return
                return foundPetDoc.toObject(PetRemote::class.java)?.copy(id = foundPetDoc.id)
            }
        } catch (e: Exception) {
            Log.e("PetRepository", "Error fetching from found_pets", e)
        }

        // Step 3: If not found in either, return null
        return null
    }

    suspend fun getAllPets(): List<PetRemote> {
        return try {
            coroutineScope {
                val lostPetsDeferred = async { db.collection("lost_pets").get().await() }
                val foundPetsDeferred = async { db.collection("found_pets").get().await() }

                val lostPetsSnapshot = lostPetsDeferred.await()
                val foundPetsSnapshot = foundPetsDeferred.await()

                // ✅ FIXED LOGIC HERE: Iterate over each document to get ID
                val lostPets = lostPetsSnapshot.documents.mapNotNull { doc ->
                    // Convert document to object and copy ID
                    doc.toObject(PetRemote::class.java)?.copy(id = doc.id)
                }
                val foundPets = foundPetsSnapshot.documents.mapNotNull { doc ->
                    // Convert document to object and copy ID
                    doc.toObject(PetRemote::class.java)?.copy(id = doc.id)
                }

                // Merge two lists and return
                lostPets + foundPets
            }
        } catch (e: Exception) {
            Log.e("PetRepository", "Error fetching all pets", e)
            emptyList()
        }
    }
}
