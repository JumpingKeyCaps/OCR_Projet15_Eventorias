package com.openclassroom.eventorias.TestConfig

import com.openclassroom.eventorias.data.repository.AuthenticationRepository
import com.openclassroom.eventorias.data.repository.EventStoreRepository
import com.openclassroom.eventorias.data.repository.StorageRepository
import com.openclassroom.eventorias.data.service.authentication.FirebaseAuthService
import com.openclassroom.eventorias.data.service.firestore.FirestoreService
import com.openclassroom.eventorias.data.service.storage.FirebaseStorageService
import com.openclassroom.eventorias.di.AppModule
import com.openclassroom.eventorias.screen.main.userProfile.UserProfileViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestAppModule {

    @Provides
    @Singleton
    fun provideMockUserProfileViewModel(): UserProfileViewModel {
        return mockk(relaxed = true)
    }

    @Provides
    @Singleton
    fun provideMockFirebaseAuthService(): FirebaseAuthService {
        return mockk(relaxed = true)
    }

    @Provides
    @Singleton
    fun provideMockFirestoreService(): FirestoreService {
        return mockk(relaxed = true)
    }

    @Provides
    @Singleton
    fun provideMockFirebaseStorageService(): FirebaseStorageService {
        return mockk(relaxed = true)
    }

    @Provides
    @Singleton
    fun provideMockAuthRepository(authService: FirebaseAuthService): AuthenticationRepository {
        return mockk(relaxed = true)
    }

    @Provides
    @Singleton
    fun provideMockEventStoreRepository(storeService: FirestoreService): EventStoreRepository {
        return mockk(relaxed = true)
    }

    @Provides
    @Singleton
    fun provideMockStorageRepository(storageService: FirebaseStorageService): StorageRepository {
        return mockk(relaxed = true)
    }
}