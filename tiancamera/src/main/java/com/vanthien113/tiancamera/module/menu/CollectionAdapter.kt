package com.vanthien113.tiancamera.module.menu

import android.media.MediaMetadataRetriever
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.vanthien113.tiancamera.R
import com.vanthien113.tiancamera.databinding.ItemCollectionBinding
import com.vanthien113.tiancamera.utils.*
import java.io.File
import kotlin.concurrent.thread

class CollectionAdapter(
    val imageLimit: Int,
    val videoLimit: Int,
    val onMediaClicked: (File) -> Unit
) : RecyclerView.Adapter<CollectionAdapter.CollectionVH>() {
    val items = mutableListOf<MediaCollection>()
    var isEnableMultipleSelect = false
    var isSelectedImageType = false

    fun updateAdapter(items: List<File>) {
        this.items.clear()
        this.items.addAll(items.map { MediaCollection(it) })
        if (!isEnableMultipleSelect) {
            /**Set first item is selected*/
            this.items.firstOrNull()?.isSelected = true
            isSelectedImageType = this.items.firstOrNull()?.file?.isImage() ?: false
        }
        notifyDataSetChanged()
    }

    fun setMultipleSelect() {
        isEnableMultipleSelect = !isEnableMultipleSelect
        /**Clear all selected state*/
        items.forEach {
            it.isSelected = false
        }

        /**Auto select first item*/
        if (!isEnableMultipleSelect) {
            items.firstOrNull()?.isSelected = true
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionVH {
        val view = ItemCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectionVH(view)
    }

    override fun onBindViewHolder(holder: CollectionVH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun getSelectedItemCount(): Int {
        return items.filter { it.isSelected }.size
    }

    inner class CollectionVH(private val binding: ItemCollectionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mediaCollection: MediaCollection) {
            val file = mediaCollection.file

            binding.ivImage.loadImageUrl(file.path)
            binding.ivSelectable.setVisible(isEnableMultipleSelect)

            itemView.setOnClickListener {
                /**If it not multiple mode, let handle save selected state*/
                if (!isEnableMultipleSelect) {
                    items.forEach { it.isSelected = false }
                    mediaCollection.isSelected = true
                    isSelectedImageType = file.isImage()
                    onMediaClicked.invoke(file)
                    return@setOnClickListener
                }

                /**Store first selected type*/
                if (getSelectedItemCount() == 0) {
                    isSelectedImageType = file.isImage()
                }

                val limit = isSelectedImageType.triadOperator(imageLimit, videoLimit)

                when {
                    /**DisSelect if file is selected*/
                    mediaCollection.isSelected -> {
                        setDisSelectedMediaInMultipleMode(binding.ivSelectable)
                        mediaCollection.isSelected = false
                    }
                    /**If file has same type of first file, let handle it*/
                    file.isImage() == isSelectedImageType && getSelectedItemCount() < limit -> {
                        setSelectedMediaInMultipleMode(binding.ivSelectable)
                        mediaCollection.isSelected = true
                        onMediaClicked.invoke(file)
                    }
                }
            }

            when {
                isEnableMultipleSelect && mediaCollection.isSelected -> {
                    setSelectedMediaInMultipleMode(binding.ivSelectable)
                }
                isEnableMultipleSelect && !mediaCollection.isSelected -> {
                    binding.ivSelectable.setImageResource(R.drawable.bg_circle_transparent)
                }
            }

            when (file.isVideo()) {
                true -> {
                    binding.tvDuration.show()

                    /**Use new thread for get duration of video instead of main thread*/
                    thread {
                        val retriever = MediaMetadataRetriever()
                        retriever.setDataSource(file.path)
                        val duration =
                            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                                ?.toLong() ?: 0L

                        Handler(Looper.getMainLooper()).post {
                            binding.tvDuration.text = DateTimeUtils.convertMillisecondToDuration(duration)
                        }
                    }
                }
                false -> {
                    binding.tvDuration.hide()
                }
            }
        }

        private fun setSelectedMediaInMultipleMode(v: ImageView) {
            v.setImageResource(R.drawable.bg_circle_black)
        }

        private fun setDisSelectedMediaInMultipleMode(v: ImageView) {
            v.setImageResource(R.drawable.bg_circle_transparent)
        }
    }
}