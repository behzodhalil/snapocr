package uz.behzoddev.snapocr

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat

fun Activity.showMessage(message: String) {
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
}

fun Activity.copyToClipboard(text: CharSequence) {
    val clipboard =
        ContextCompat.getSystemService(this, ClipboardManager::class.java)
    val clip = ClipData.newPlainText("label", text)
    clipboard?.setPrimaryClip(clip)
    showMessage(getString(R.string.clipboard_text))
}

fun Activity.shareText(text: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(Intent.createChooser(shareIntent, getString(R.string.share_text_title)))
}

fun Activity.isTextValid(text: String?): Boolean {
    return !text.isNullOrBlank() && text != getString(R.string.no_text_found)
}