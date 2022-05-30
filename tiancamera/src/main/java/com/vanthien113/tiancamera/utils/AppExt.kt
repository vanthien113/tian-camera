package com.vanthien113.tiancamera.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.webkit.MimeTypeMap
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.vanthien113.tiancamera.R
import java.io.File
import java.io.FileOutputStream
import java.util.*

fun Fragment.setStatusBarIconColor(isLight: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val decor: View? = this.activity?.window?.decorView
        decor?.let { view ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                view.windowInsetsController?.setSystemBarsAppearance(
                    APPEARANCE_LIGHT_NAVIGATION_BARS,
                    APPEARANCE_LIGHT_STATUS_BARS
                )

            } else {
                if (isLight) {
                    view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                } else {
                    view.setSystemUiVisibility(0)
                }
            }
        }

    }
}

fun Activity.setStatusBarColor(colorId: Int) {
    window?.statusBarColor = ContextCompat.getColor(this, colorId)
}

/**
 * return pattern "[applicationId]/data/@params[folderName]/"
 */
fun Context.createInternalFolderIfNeeded(folderName: String): String {
    val path = "${this.cacheDir}/$folderName/"
    val folder = File(path)
    if (!folder.exists()) {
        folder.mkdir() //If there is no folder it will be created.
    }
    return folder.absolutePath
}

fun Context.hasPermission(permission: String): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    } else true
}

fun Context.hasPermissions(permissions: List<String>): Boolean {
    var hasAll = true
    permissions.forEach {
        if (!hasPermission(it)) {
            hasAll = false
        }
    }
    return hasAll
}

fun Context.getScreenSize(): DisplayMetrics {
    return this.resources.displayMetrics
}

fun Context?.getInt(resId: Int): Int {
    return this?.resources?.getInteger(resId) ?: 0
}

fun File.getMimeType(): String {
    var type: String? = null
    val url = this.toString()
    val extension: String = MimeTypeMap.getFileExtensionFromUrl(url)
    type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase(Locale.ROOT))
    if (type == null) {
        type = "image/*" // fallback type. You might set it to */*
    }
    return type
}

fun File.isImage(): Boolean {
    return getMimeType() == "image/jpeg"
}

fun File.isVideo(): Boolean {
    return getMimeType() == "video/mp4"
}

fun ImageView.loadUrl(link: String) {
    Glide.with(this)
        .load(link)
        .apply(RequestOptions().override(1200, 1200))
        .centerInside()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}

fun Context.loadUrl(link: String, callback: (Bitmap) -> Any?) {
    Glide.with(this)
        .asBitmap()
        .load(link)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                callback.invoke(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                // just nothing now
            }
        })
}

fun View.getBitmap(): Bitmap {
    measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY)
    val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val c = Canvas(b)
    layout(left, top, right, bottom)
    draw(c)
    return b
}

fun Bitmap.small(): Bitmap {
    return Bitmap.createScaledBitmap(
        this,
        120,
        120,
        false
    )
}

fun Bitmap.toFile(context: Context, fileName: String, quality: Int = 100): File? {
    val dir = File(context.filesDir, "images")
    if (!dir.exists()) {
        dir.mkdir()
    }
    val mypath = File(dir, fileName)
    var fos: FileOutputStream? = null
    return try {
        fos = FileOutputStream(mypath);
        // Use the compress method on the BitMap object to write image to the OutputStream
        compress(Bitmap.CompressFormat.PNG, quality, fos)
        mypath
    } catch (e: Exception) {
        null
    } finally {
        fos?.close()
    }
}

fun <R> Boolean.then(block: () -> R): R? {
    return if (this) block.invoke()
    else null
}

fun WebView.onLoadContentProductDetail(
    isDarkMode: Boolean,
    description: String,
    descriptionImages: List<String>,
    onPageFinished: ((View?) -> Unit)? = null
) {
    val textColor = if (isDarkMode) "#FFFFFF" else "#1A1A1A"
    val metaString = "<meta charset=\"UTF-8\">" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=yes\">"
    val cssStyleString = "<style type=\"text/css\">" +
            "@font-face { font-family: lexend; src: url(\"file:///android_asset/fonts/lexend_regular.ttf\")}" +
            "body { padding: 16px; margin: 0px; font-family: lexend; font-size: 14px; text-align: justify; color: $textColor; }" +
            "img { max-width: 100%; margin-top: 6px; }" +
            "</style>"
    var htmlDescription = description.replace("\n", "</br>")
    descriptionImages.forEach { imageUrl ->
        htmlDescription += "</br><img src=\"$imageUrl\">"
    }
    val htmlString = "<html><head>$metaString</head>$cssStyleString<body>$htmlDescription</body></html>"

    this.apply {
        loadDataWithBaseURL("file:///android_asset/", htmlString, "text/html; charset=UTF-8", "UTF-8", null)
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        settings.defaultTextEncodingName = "UTF-8"
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        isHorizontalScrollBarEnabled = false
        isVerticalScrollBarEnabled = false
        isEnabled = false
        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                onPageFinished?.invoke(view)
            }
        }
    }
}

fun WebView.onLoadContent(
    content: String,
    isDarkMode: Boolean,
    onPageFinished: ((View?) -> Unit)? = null
) {
    val textColor = if (isDarkMode) "#FFFFFF" else "#1A1A1A"
    val metaString = "<meta charset=\"UTF-8\">" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no\">"
    val cssStyleString = "<style type=\"text/css\">" +
            "@font-face { font-family: lexend; src: url(\"file:///android_asset/fonts/lexend_regular.ttf\")}" +
            "body { padding: 16px; margin: 0px; font-family: lexend; font-size: 14px; text-align: justify; color: $textColor; }" +
            "img { max-width: 100%; margin-top: 6px; }" +
            "</style>"
    val htmlString = "<html><head>$metaString</head>$cssStyleString<body>${content.replace("\n", "</br>")}</body></html>"
    this.apply {
        loadDataWithBaseURL("file:///android_asset/", htmlString, "text/html; charset=UTF-8", "UTF-8", null)
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        settings.defaultTextEncodingName = "UTF-8"
        isHorizontalScrollBarEnabled = false
        isVerticalScrollBarEnabled = false
        isEnabled = false
        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                onPageFinished?.invoke(view)
            }
        }
    }
}