package com.example.petpal.di

import com.example.petpal.domain.repository.PetRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Provide dependencies for the entire application
object AppModule {

    /**
     * Teach Hilt how to create a FirebaseFirestore object.
     * @Singleton ensures only ONE instance is created.
     */
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    /**
     * Teach Hilt how to create PetRepository.
     * Hilt will see that this function needs a FirebaseFirestore.
     * It will automatically find the provideFirestore() function above to get the object and pass it here.
     */
    @Provides
    @Singleton
    fun providePetRepository(db: FirebaseFirestore): PetRepository {
        return PetRepository(db)
    }
}
