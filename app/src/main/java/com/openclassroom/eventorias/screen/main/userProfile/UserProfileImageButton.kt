package com.openclassroom.eventorias.screen.main.userProfile

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.openclassroom.eventorias.R

/**
 * Composable function to display a user profile image with an option to select a new image in gallery.
 * @param userProfileImageUrl The URL of the user's profile image.
 * @param onImageSelected A callback function to handle the selected image URI.
 */
@Composable
fun UserProfileImageButton(
    userProfileImageUrl: String?,
    onImageSelected: (Uri?) -> Unit
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher for the image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        onImageSelected(uri)
    }

    // Handle permission requests for Android 13+ if needed
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Determine which image to display
    val imagePainter = if (selectedImageUri != null) {
        rememberAsyncImagePainter(model = selectedImageUri)
    } else {
        rememberAsyncImagePainter(
            model = userProfileImageUrl,
            placeholder = painterResource(id = R.drawable.placeholder_profile),
            error = painterResource(id = R.drawable.placeholder_profile)
        )
    }

    // ImageButton composable
    Image(
        painter = imagePainter,
        contentDescription = "User Profile Picture",
        modifier = Modifier
            .padding(end = 12.dp)
            .size(42.dp)
            .clip(CircleShape)
            .clickable {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permission = android.Manifest.permission.READ_MEDIA_IMAGES
                    if (ContextCompat.checkSelfPermission(context, permission) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        imagePickerLauncher.launch("image/*")
                    } else {
                        permissionLauncher.launch(permission)
                    }
                } else {
                    // For older Android versions, no runtime permission is required
                    imagePickerLauncher.launch("image/*")
                }
            },
        contentScale = ContentScale.Crop
    )
}