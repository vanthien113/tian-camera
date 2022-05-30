package com.vanthien113.tiancamera.module.menu

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.LifecycleOwner
import com.vanthien113.tiancamera.R
import com.vanthien113.tiancamera.base.BaseFragment
import com.vanthien113.tiancamera.databinding.FragmentCustomizeRecordVideoBinding
import com.vanthien113.tiancamera.module.main.PickMediaFragment
import com.vanthien113.tiancamera.utils.DateTimeUtils
import com.vanthien113.tiancamera.utils.applyBackgroundStream
import com.vanthien113.tiancamera.utils.hide
import com.vanthien113.tiancamera.utils.show
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.addTo
import java.util.concurrent.TimeUnit

/**
 * Handle record video and return video path through activity result
 */
class CustomizeVideoRecordFragment : BaseFragment<FragmentCustomizeRecordVideoBinding>() {
    private var cameraProvider: ProcessCameraProvider? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var camera: Camera? = null
    private var isEnabledTorch = false
    private lateinit var videoCapture: VideoCapture<Recorder>
    private var recording: Recording? = null
//    private val disposable = CompositeDisposable()

    companion object {
        const val VIDEO_PATH_RESULT = "video_path_result"
        const val LIMIT_TIME = 20L
    }

    override fun inflateViewBinding(): FragmentCustomizeRecordVideoBinding {
        return FragmentCustomizeRecordVideoBinding.inflate(layoutInflater)
    }

    override fun initialize() {
        startCamera()

        listenClickEvent()
    }

    /**
     * Handle view click's event
     */
    private fun listenClickEvent() {
        getBinding().ivRecordVideo.setOnClickListener {
            captureVideo()
        }

        getBinding().tvCancel.setOnClickListener {
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

        getBinding().tvTakePictureMenu.setOnClickListener {
            (parentFragment as? PickMediaFragment)?.replacePhotoFragment()
        }

        getBinding().tvCollectionMenu?.setOnClickListener {
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

        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build()

        videoCapture = VideoCapture.withOutput(recorder)

        camera = cameraProvider.bindToLifecycle(
            this as LifecycleOwner,
            cameraSelector,
            preview,
            videoCapture
        )
    }

    /**
     * Handle record video and store it to local storage
     */
    private fun captureVideo() {
        val curRecording = recording
        if (curRecording != null) {
            // Stop the current recording session.
            curRecording.stop()
            resetRecordState()
            showLoading(false)
            recording = null
            return
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis())
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/Bidu-Video")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(requireContext().contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()

        recording = videoCapture.output
            .prepareRecording(requireContext(), mediaStoreOutputOptions)
            .apply {
                if (PermissionChecker.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PermissionChecker.PERMISSION_GRANTED) {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(requireContext())) { recordEvent ->
                hideLoading()
                when (recordEvent) {
                    is VideoRecordEvent.Start -> {
                        startRecordState()
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            // Record success
                            trimVideo(recordEvent.outputResults.outputUri)
                        } else {
                            // Record error
                            resetRecordState()
                        }
                    }
                }
            }
    }

    private fun trimVideo(videoUri: Uri) {
//        if (context == null) {
//            return
//        }
//        val path = FileUtils.getRealPathFromURI(requireContext(), videoUri) ?: ""
//
//        val file = Uri.fromFile(File(path))
//
//        val length: Long = CustomizeVideoRecordActivity.LIMIT_TIME
//
//        val trimOption = TrimVideoOptions().apply {
//            trimType = TrimType.DEFAULT
//            compressOption = CompressOption().apply {
//                // height = 1200
//                // width = 1200
//                trimType = TrimType.MIN_MAX_DURATION
//                minToMax = longArrayOf(5, length) // min 5 sec, max is 15/30/60/90 sec
//            }
//        }
//        val intent = Intent(context, ActVideoTrimmer::class.java).apply {
//            putExtra(TrimVideo.TRIM_VIDEO_URI, file.toString())
//            putExtra(TrimVideo.TRIM_VIDEO_OPTION, trimOption)
//        }
//        startActivityForResult(intent, TrimVideo.VIDEO_TRIMMER_REQ_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        Timber.e("onActivityResult $requestCode, $resultCode, $data")
//
//        if (requestCode == TrimVideo.VIDEO_TRIMMER_REQ_CODE && data != null && resultCode == Activity.RESULT_OK) {
//            val uri = Uri.parse(TrimVideo.getTrimmedVideoPath(data))
//            Timber.d("Trimmed path:: $uri")
//
//            returnResult(uri)
//        }
    }


    /**
     * Change UI to recording
     */
    private fun startRecordState() {
        startCountDown()
        getBinding().cvRecordTime.show()
        getBinding().tvTakePictureMenu.hide()
        getBinding().tvCollectionMenu.hide()
        getBinding().ivRotateCamera.hide()
        getBinding().ivRecordVideo.setImageResource(R.drawable.ic_stop_record_video)
    }

    /**
     * Reset UI to default state
     */
    private fun resetRecordState() {
        getBinding().tvTakePictureMenu.show()
        getBinding().tvCollectionMenu.show()
        getBinding().cvRecordTime.hide()
        getBinding().ivRotateCamera.show()
        getBinding().ivRecordVideo.setImageResource(R.drawable.ic_record_video)
        stopCountDown()
    }

    /**
     * Handle return result to activity result
     * @param uri uri of video stored
     */
    private fun returnResult(uri: Uri) {
        (parentFragment as? PickMediaFragment)?.videoResult(uri.path.toString())
    }

    /**
     * Start and show countdown time
     */
    private fun startCountDown() {
        Observable
            .interval(1, TimeUnit.SECONDS)
            .applyBackgroundStream()
            .subscribe {
                val time = DateTimeUtils.convertMillisecondToDurationFullFormat(it * 1000)
                getBinding().tvCountDownTimer.text = time

                if (it == LIMIT_TIME) {
                    captureVideo()
                    stopCountDown()
                }
            }
            .addTo(disposable)
    }

    /**
     * Stop countdown UI
     */
    private fun stopCountDown() {
        disposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraProvider?.unbindAll()
        disposable.clear()
    }
}