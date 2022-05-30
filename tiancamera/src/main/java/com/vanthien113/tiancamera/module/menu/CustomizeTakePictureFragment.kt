package com.vanthien113.tiancamera.module.menu

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.vanthien113.tiancamera.module.main.PickMediaFragment
import com.vanthien113.tiancamera.base.BaseFragment
import com.vanthien113.tiancamera.databinding.FragmentCustomizeTakePictureBinding
import com.vanthien113.tiancamera.utils.FileUtils
import com.vanthien113.tiancamera.utils.hide

/**
 * Handle take image and result image path through activity result
 */
class CustomizeTakePictureFragment : BaseFragment<FragmentCustomizeTakePictureBinding>() {
    private var cameraProvider: ProcessCameraProvider? = null
    private val imageCapture = ImageCapture.Builder().build()
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var camera: Camera? = null
    private var isEnabledTorch = false
    private var imageUri: Uri? = null

    companion object {
        const val IMAGE_URI_RESULT = "uri_result"
        private const val CODE_FILTER_IMAGE = 101
    }

    override fun inflateViewBinding(): FragmentCustomizeTakePictureBinding {
        return FragmentCustomizeTakePictureBinding.inflate(layoutInflater)
    }

    override fun initialize() {
        startCamera()

        listenClickEvent()
    }

    /**
     * Handle view click's event
     */
    private fun listenClickEvent() {
        getBinding().ivTakePicture.setOnClickListener {
            takePhoto()
        }

        getBinding().tvCancel.setOnClickListener {
            activity?.finish()
        }

        getBinding().tvNext.setOnClickListener {
            activity?.setResult(Activity.RESULT_OK, Intent().apply {
                imageUri?.let {
                    kotlin.runCatching {
                        val path = FileUtils.getRealPathFromURI(requireContext(), it)
                        putExtra(IMAGE_URI_RESULT, path)
                    }.onFailure {
                        Log.d("THIEN", "Get image path error : ${it.message}")
                    }
                }
            })
            activity?.finish()
        }

        getBinding().ivRotateCamera.setOnClickListener {
            cameraProvider?.unbindAll()
            flipCamera()
            startCamera()
        }

        getBinding().ivFlash.setOnClickListener {
            isEnabledTorch = !isEnabledTorch
            camera?.cameraControl?.enableTorch(isEnabledTorch)
        }

        getBinding().tvCancelResult.setOnClickListener {
            imageUri = null
            getBinding().clResult.hide()
        }

        getBinding().tvVideo.setOnClickListener {
            (parentFragment as? PickMediaFragment)?.replaceVideoFragment()
        }

        getBinding().clResult.setOnClickListener { }

        getBinding().tvCollectionMenu.setOnClickListener {
            (parentFragment as? PickMediaFragment)?.replaceCollectionFragment()
        }
    }

    /**
     * Get and listen camera device
     */
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            this.cameraProvider = cameraProvider
            cameraProvider?.let { bindPreview(it) }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    /**
     * Change Camera position to Front or Back
     */
    private fun flipCamera() {
        when (lensFacing) {
            CameraSelector.LENS_FACING_FRONT -> lensFacing = CameraSelector.LENS_FACING_BACK
            CameraSelector.LENS_FACING_BACK -> lensFacing = CameraSelector.LENS_FACING_FRONT
        }
    }

    /**
     * Bind camera preview
     * @param cameraProvider
     */
    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder().build()

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        preview.setSurfaceProvider(getBinding().previewView.surfaceProvider)
        camera = cameraProvider.bindToLifecycle(
            this as LifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
    }

    /**
     * Handle take photo and store it to local storage
     */
    private fun takePhoto() {
        showLoading(false)

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis())
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Bidu-Images")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                requireContext().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    showToast("Photo capture failed")
                    hideLoading()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    output.savedUri?.let {
                        FileUtils.getRealPathFromURI(requireContext(), it)?.let {
                            filterPhoto(it)
                        }
                    }
                    hideLoading()
                }
            }
        )
    }

    private fun filterPhoto(path: String) {
//        try {
//            val outputFolderPath = requireContext().createInternalFolderIfNeeded("image")
//            val intent = ImageEditorIntentBuilder(
//                requireContext(),
//                path,
//                "$outputFolderPath${System.currentTimeMillis() / 1000}.jpg"
//            )
//                .withAddText() // Add the features you need
//                .withFilterFeature()
//                .withRotateFeature()
//                .withCropFeature()
//                .withBrightnessFeature()
//                .withSaturationFeature()
//                .withBeautyFeature()
//                .withStickerFeature()
//                .forcePortrait(true)
//                .build()
//            startActivityForResult(intent, CODE_FILTER_IMAGE)
//        } catch (e: Exception) {
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CODE_FILTER_IMAGE && resultCode == Activity.RESULT_OK) { // same code you used while starting
//            val isImageEdit = data?.getBooleanExtra(EditImageActivity.IS_IMAGE_EDITED, false)
//            val path: String = if (isImageEdit == true) {
//                data?.getStringExtra(ImageEditorIntentBuilder.OUTPUT_PATH).orEmpty()
//            } else {
//                data?.getStringExtra(ImageEditorIntentBuilder.SOURCE_PATH).orEmpty()
//            }

//            (parentFragment as? PickMediaFragment)?.imageResult(path)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraProvider?.unbindAll()
    }
}