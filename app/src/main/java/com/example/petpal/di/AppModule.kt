package com.example.petpal.di


import com.example.petpal.domain.repository.PetRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Cung cấp dependency cho toàn bộ ứng dụng
object AppModule {

    /**
     * Dạy cho Hilt cách tạo ra một đối tượng FirebaseFirestore.
     * @Singleton đảm bảo chỉ có MỘT đối tượng duy nhất được tạo ra.
     */
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    /**
     * Dạy cho Hilt cách tạo ra PetRepository.
     * Hilt sẽ thấy hàm này cần một FirebaseFirestore.
     * Nó sẽ tự tìm hàm provideFirestore() ở trên để lấy đối tượng và truyền vào đây.
     */
    @Provides
    @Singleton
    fun providePetRepository(db: FirebaseFirestore): PetRepository {
        return PetRepository(db)
    }
}