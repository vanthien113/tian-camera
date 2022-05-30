package com.vanthien113.tiancamera.module.menu

import android.os.Environment
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.vanthien113.tiancamera.module.main.PickMediaFragment
import com.vanthien113.tiancamera.R
import com.vanthien113.tiancamera.base.BaseFragment
import com.vanthien113.tiancamera.databinding.FragmentCustomizeCollectionBinding
import com.vanthien113.tiancamera.utils.*
import java.io.File

class CustomizeCollectionFragment : BaseFragment<FragmentCustomizeCollectionBinding>() {
    private val collections = mutableListOf<File>()
    private val collectionAdapter by lazy {
        CollectionAdapter(
            imageLimit = IMAGE_LIMIT,
            videoLimit = VIDEO_LIMIT,
            onMediaClicked = { onMediaClicked(it) })
    }

    companion object {
        private const val IMAGE_LIMIT = 5
        private const val VIDEO_LIMIT = 1
    }

    override fun inflateViewBinding(): FragmentCustomizeCollectionBinding {
        return FragmentCustomizeCollectionBinding.inflate(layoutInflater)
    }

    override fun initialize() {
        initRV()
        getCollections()

        getBinding().ivZoom.setOnClickListener {
            getBinding().ivZoomable.resetScaleImage()
        }

        getBinding().tvCancel.setOnClickListener {
            activity?.finish()
        }

        getBinding().tvTakeImage.setOnClickListener {
            (parentFragment as? PickMediaFragment)?.replacePhotoFragment()
        }

        getBinding().tvRecordVideo.setOnClickListener {
            (parentFragment as? PickMediaFragment)?.replaceVideoFragment()
        }

        getBinding().tvContinue.setOnClickListener {
            if (collectionAdapter.isSelectedImageType) {
                (parentFragment as? PickMediaFragment)?.imagesResult(collectionAdapter.items.filter { it.isSelected }.map { it.file.path })
            } else {
                (parentFragment as? PickMediaFragment)?.videosResult(collectionAdapter.items.filter { it.isSelected }.map { it.file.path })
            }
        }

        getBinding().ivMultiSelect.setOnClickListener {
            if (collectionAdapter.isEnableMultipleSelect) {
                getBinding().ivMultiSelect.setImageResource(R.drawable.ic_multi_select_white)
            } else {
                getBinding().ivMultiSelect.setImageResource(R.drawable.ic_multi_select_black)
            }
            collectionAdapter.setMultipleSelect()
        }
    }

    private fun initRV() {
        getBinding().rvMedias.adapter = collectionAdapter
        getBinding().rvMedias.layoutManager = GridLayoutManager(context, 4)
        getBinding().rvMedias.addItemDecoration(GridSpacingItemDecoration(4, 1f.dpToPx(requireContext()), true))
    }

    /**
     * Get all collection name from DCIM, PICTURES and MOVIES
     */
    private fun getCollections() {
        val tempCollections = mutableListOf<File>()
        val dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).listFiles()
        val pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).listFiles()
        val movies = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).listFiles()
        tempCollections.addAll(dcim)
        tempCollections.addAll(pictures)
        tempCollections.addAll(movies)

        /**Remove directory name with . at first. Such as .thumbnail*/
        collections.addAll(tempCollections
            .filter { !it.isFile }
            .filter { it.name.firstOrNull() != '.' }
        )

        val adapter = ArrayAdapter(requireContext(), R.layout.item_customize_collection_spinner, collections.map { it.name })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        getBinding().snTakePictureTitle.adapter = adapter
        getBinding().snTakePictureTitle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                getAllFileInPath(collections[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    /**
     * Get all images/videos in folder
     */
    private fun getAllFileInPath(file: File) {
        val files = File(file.path).listFiles()?.toList() ?: listOf()
        val filteredFiles = files.filter { it.isImage() || it.isVideo() }
        collectionAdapter.updateAdapter(filteredFiles)

        files.firstOrNull()?.let {
            getBinding().ivZoomable.loadImageUrl(it.path)
            getBinding().tvContinue.show()
        } ?: kotlin.run {
            getBinding().ivZoomable.setImageResource(android.R.color.transparent)
            getBinding().tvContinue.hide()
        }
    }

    private fun onMediaClicked(file: File) {
        getBinding().ivZoomable.loadImageUrl(file.path)
    }
}