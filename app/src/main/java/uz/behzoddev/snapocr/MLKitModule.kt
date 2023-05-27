package uz.behzoddev.snapocr

import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MLKitModule {

    @Provides
    @Singleton
    fun providesTextRecognition(): TextRecognizer {
        return TextRecognition.getClient()
    }
}