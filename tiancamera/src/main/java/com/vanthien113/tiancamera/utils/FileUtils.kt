package com.vanthien113.tiancamera.utils

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    private fun getFileDir(): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    }

    fun saveBitmapToExternalStorage(bitmap: Bitmap): String? {
        val fileDir = File("${getFileDir()}/bidu")
        if (!fileDir.exists())
            fileDir.mkdirs()

        val fileName = "bidu_${System.currentTimeMillis()}.jpg"

        val file = File(fileDir, fileName)
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
        out.close()

        return Uri.fromFile(file).path
    }

    /**
     * Get real path of file from file uri
     * @param context
     * @param contentURI file uri
     */
    fun getRealPathFromURI(context: Context, contentURI: Uri): String? {
        var result: String? = null
        val cursor: Cursor? = context.contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) {
            result = contentURI.path
        } else {
            if (cursor.moveToFirst()) {
                val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                result = cursor.getString(idx)
            }
            cursor.close()
        }
        return result
    }
}