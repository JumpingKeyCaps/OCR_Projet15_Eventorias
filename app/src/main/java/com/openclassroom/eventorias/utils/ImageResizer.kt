package com.openclassroom.eventorias.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Redimensionne une image à une taille maximale spécifiée, tout en respectant le ratio de l'image.
 * @param context Le contexte pour accéder au content resolver.
 * @param uri L'URI de l'image à redimensionner.
 * @param maxWidth La largeur maximale de l'image. Si elle est définie à -1, la largeur ne sera pas modifiée.
 * @param maxHeight La hauteur maximale de l'image. Si elle est définie à -1, la hauteur ne sera pas modifiée.
 * @return Un URI temporaire de l'image redimensionnée.
 */
fun resizeImage(context: Context, uri: Uri, maxWidth: Int = -1, maxHeight: Int = -1): Uri? {
    val inputStream = context.contentResolver.openInputStream(uri)
    val originalBitmap = BitmapFactory.decodeStream(inputStream)
    val aspectRatio = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()

    val newWidth: Int
    val newHeight: Int

    // Si maxWidth et maxHeight sont tous les deux définis, on respecte les deux dimensions
    if (maxWidth > 0 && maxHeight > 0) {
        // Calculer les nouvelles dimensions tout en respectant le ratio
        val widthRatio = originalBitmap.width.toFloat() / maxWidth.toFloat()
        val heightRatio = originalBitmap.height.toFloat() / maxHeight.toFloat()
        val maxRatio = Math.max(widthRatio, heightRatio)

        newWidth = (originalBitmap.width / maxRatio).toInt()
        newHeight = (originalBitmap.height / maxRatio).toInt()
    }
    // Si seul maxWidth est défini, calculer la hauteur automatiquement en fonction du ratio
    else if (maxWidth > 0) {
        newWidth = maxWidth
        newHeight = (newWidth / aspectRatio).toInt()
    }
    // Si seul maxHeight est défini, calculer la largeur automatiquement en fonction du ratio
    else if (maxHeight > 0) {
        newHeight = maxHeight
        newWidth = (newHeight * aspectRatio).toInt()
    } else {
        // Si aucune dimension n'est spécifiée, renvoyer l'original
        return uri
    }

    // Redimensionner l'image
    val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)

    // Enregistrer l'image redimensionnée dans un fichier temporaire
    val tempFile = File(context.cacheDir, "resized_image.jpg")
    try {
        val outputStream = FileOutputStream(tempFile)
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        outputStream.flush()
        outputStream.close()
        return Uri.fromFile(tempFile)
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return null
}