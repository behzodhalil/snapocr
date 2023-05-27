package uz.behzoddev.snapocr

import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.text.TextRecognizer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SnapOcrViewModel @Inject constructor(
    private val textRecognizer: TextRecognizer
) : ViewModel() {


}