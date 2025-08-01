package com.example.petpal.domain.repository


import com.example.petpal.domain.model.PetRemote
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PetRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    fun fetchLostPets(onResult: (List<PetRemote>) -> Unit) {
        db.collection("lost_pets")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val pets = result.documents.mapNotNull { doc ->
                    val pet = doc.toObject(PetRemote::class.java)
                    pet?.copy(id = doc.id)
                }
                onResult(pets)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun fetchFoundPets(onResult: (List<PetRemote>) -> Unit) {
        db.collection("found_pets")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val pets = result.documents.mapNotNull { doc ->
                    val pet = doc.toObject(PetRemote::class.java)
                    pet?.copy(id = doc.id)
                }
                onResult(pets)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}
