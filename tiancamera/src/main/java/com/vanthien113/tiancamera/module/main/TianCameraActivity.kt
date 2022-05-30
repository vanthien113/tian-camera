package com.vanthien113.tiancamera.module.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vanthien113.tiancamera.R

class TianCameraActivity : AppCompatActivity() {
    private val mediaType by lazy { intent.getIntExtra(MEDIA_TYPE, 0) }

    companion object {
        /**
         * 0: image
         * 1: video
         * 2: gallery
         */
        private const val MEDIA_TYPE = "media_type"

        fun startTakeImageActivity(context: Context) {
            context.startActivity(Intent(context, TianCameraActivity::class.java).apply {
                putExtra(MEDIA_TYPE, 0)
            })
        }

        fun startRecordVideoActivity(context: Context) {
            context.startActivity(Intent(context, TianCameraActivity::class.java).apply {
                putExtra(MEDIA_TYPE, 1)
            })
        }

        fun startGalleryActivity(context: Context) {
            context.startActivity(Intent(context, TianCameraActivity::class.java).apply {
                putExtra(MEDIA_TYPE, 2)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tian_camera)

        supportFragmentManager
            .beginTransaction()
            .replace(
                android.R.id.content,
                PickMediaFragment().apply {
                    arguments = Bundle().apply {
                        val type = when (mediaType) {
                            0 -> PickMediaFragment.TYPE_IMAGE
                            1 -> PickMediaFragment.TYPE_VIDEO
                            else -> PickMediaFragment.TYPE_GALLERY
                        }

                        putString(PickMediaFragment.TYPE, type)
                    }
                },
                PickMediaFragment::class.java.name
            )
            .commit()
    }
}