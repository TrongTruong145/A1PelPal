package com.example.petpal

import android.content.Context
import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

suspend fun uploadImagesToFirebase(imageUris: List<Uri>, context: Context): List<String> {
    val storage = Firebase.storage
    val downloadUrls = mutableListOf<String>()

    for (uri in imageUris) {
        val fileName = "lost_pets/${System.currentTimeMillis()}_${uri.lastPathSegment}"
        val ref = storage.reference.child(fileName)

        val uploadTask = ref.putFile(uri)
        uploadTask.await()  // dùng kotlinx-coroutines-play-services

        val downloadUrl = ref.downloadUrl.await()
        downloadUrls.add(downloadUrl.toString())
    }

    return downloadUrls
}
