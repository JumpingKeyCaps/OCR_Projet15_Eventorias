package com.openclassroom.eventorias.di

import com.openclassroom.eventorias.data.repository.AuthenticationRepository
import com.openclassroom.eventorias.data.repository.UserStoreRepository
import com.openclassroom.eventorias.data.service.authentication.FirebaseAuthService
import com.openclassroom.eventorias.data.service.firestore.FirestoreService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * This class acts as a Dagger Hilt module, responsible for providing dependencies to other parts of the application.
 * It's installed in the SingletonComponent, ensuring that dependencies provided by this module are created only once
 * and remain available throughout the application's lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    /**
     * Provide a singleton instance of Authentication Repository
     * @param authService The Authentication Service
     * @return The Authentication Repository
     */
    @Provides
    @Singleton
    fun provideAuthRepository(authService: FirebaseAuthService): AuthenticationRepository {
        return AuthenticationRepository(authService)
    }

    /**
     * Provides a Singleton instance of FirebaseAuthService.
     * @return A Singleton instance of FirebaseAuthService.
     */
    @Provides
    @Singleton
    fun provideFirebaseAuthService(): FirebaseAuthService {
        return FirebaseAuthService()
    }


    /**
     * Provides a Singleton instance of FirestoreService.
     * @return A Singleton instance of FirestoreService.
     */
    @Provides
    @Singleton
    fun provideFirestoreService(): FirestoreService {
        return FirestoreService()
    }

    /**
     * Provides a Singleton instance of UserStoreRepository.
     * @param storeService The FirestoreService instance.
     * @return A Singleton instance of UserStoreRepository.
     */
    @Provides
    @Singleton
    fun provideUserStoreRepository(storeService: FirestoreService): UserStoreRepository {
        return UserStoreRepository(storeService)
    }


}