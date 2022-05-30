package com.vanthien113.demo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.vanthien113.demo.databinding.ActivityDemoBinding
import com.vanthien113.tiancamera.module.main.TianCameraActivity

class DemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        binding.tvGallery.setOnClickListener { TianCameraActivity.startGalleryActivity(this) }
        binding.tvRecordVideo.setOnClickListener { TianCameraActivity.startRecordVideoActivity(this) }
        binding.tvTakeImage.setOnClickListener { TianCameraActivity.startTakeImageActivity(this) }
    }
}