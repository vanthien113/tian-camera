package com.vanthien113.tiancamera.module.main

import androidx.fragment.app.FragmentTransaction
import com.vanthien113.tiancamera.base.BaseFragment
import com.vanthien113.tiancamera.databinding.FragmentPickMediaBinding
import com.vanthien113.tiancamera.module.menu.CustomizeCollectionFragment
import com.vanthien113.tiancamera.module.menu.CustomizeTakePictureFragment
import com.vanthien113.tiancamera.module.menu.CustomizeVideoRecordFragment

class PickMediaFragment : BaseFragment<FragmentPickMediaBinding>() {
    private val type by lazy { arguments?.getString(TYPE) }

    companion object {
        const val TYPE = "type"
        const val TYPE_VIDEO = "video"
        const val TYPE_IMAGE = "image"
        const val TYPE_GALLERY = "gallery"
    }

    override fun inflateViewBinding(): FragmentPickMediaBinding {
        return FragmentPickMediaBinding.inflate(layoutInflater)
    }

    override fun initialize() {
        when (type) {
            TYPE_IMAGE -> replacePhotoFragment()
            TYPE_VIDEO -> replaceVideoFragment()
            TYPE_GALLERY -> replaceCollectionFragment()
        }
    }

    fun replacePhotoFragment() {
        childFragmentManager
            .beginTransaction()
            .replace(getBinding().fragmentContainer.id, CustomizeTakePictureFragment())
            .commit()
    }

    fun replaceVideoFragment() {
        childFragmentManager
            .beginTransaction()
            .replace(getBinding().fragmentContainer.id, CustomizeVideoRecordFragment())
            .commit()
    }

    fun replaceCollectionFragment() {
        childFragmentManager
            .beginTransaction()
            .replace(getBinding().fragmentContainer.id, CustomizeCollectionFragment())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    /**
     * Set image result used for take camera feature
     */
    fun imageResult(imagePath: String) {
//        viewModel.addPhoto(MediaLink(imagePath, MediaLink.MediaType.PATH))
//        viewModel.requestSubmitInputMedia()
    }

    /**
     * Set image result used for record video feature
     */
    fun videoResult(videoPath: String) {
//        viewModel.addVideo(MediaLink(videoPath, MediaLink.MediaType.PATH))
//        viewModel.requestSubmitInputMedia()
    }

    /**
     * Set image result used for select image from collection feature
     */
    fun imagesResult(imagePath: List<String>) {
//        viewModel.switchToPhotoMode()
//        imagePath.forEach {
//            viewModel.addPhoto(MediaLink(it, MediaLink.MediaType.PATH))
//        }
//        viewModel.requestSubmitInputMedia()
    }

    /**
     * Set image result used for select video from collection feature
     */
    fun videosResult(videoPath: List<String>) {
//        viewModel.switchToVideoMode()
//        videoPath.forEach {
//            viewModel.addVideo(MediaLink(it, MediaLink.MediaType.PATH))
//        }
//        viewModel.requestSubmitInputMedia()
    }
}