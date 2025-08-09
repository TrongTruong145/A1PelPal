package com.example.petpal.domain.repository


import com.example.petpal.domain.model.PetRemote
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Date

class PetRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    fun fetchLostPets(onResult: (List<PetRemote>) -> Unit) {
        db.collection("lost_pets")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val pets = result.documents.mapNotNull { doc ->
                    try {
                        // Ánh xạ thủ công để đảm bảo các trường khớp
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
                            // Ánh xạ timestamp
                            timestamp = (doc.getTimestamp("timestamp"))?.toDate()
                        )
                    } catch (e: Exception) {
                        // Xử lý lỗi nếu việc ánh xạ thất bại
                        null
                    }
                }
                onResult(pets)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun fetchFoundPets(onResult: (List<PetRemote>) -> Unit) {
        db.collection("lost_pets")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val pets = result.documents.mapNotNull { doc ->
                    try {
                        // Ánh xạ thủ công để đảm bảo các trường khớp
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
                            // Ánh xạ timestamp
                            timestamp = (doc.getTimestamp("timestamp"))?.toDate()
                        )
                    } catch (e: Exception) {
                        // Xử lý lỗi nếu việc ánh xạ thất bại
                        null
                    }
                }
                onResult(pets)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun addLostPet(pet: PetRemote, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("lost_pets")
            .add(pet.copy(timestamp = Date())) // Tự động thêm timestamp trước khi ghi
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun addFoundPet(pet: PetRemote, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("found_pets")
            .add(pet.copy(timestamp = Date())) // Tự động thêm timestamp trước khi ghi
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }


}
