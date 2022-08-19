package com.example.happyplaceapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.happyplaceapp.databinding.ActivityHappyPlaceDetailsBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HappyPlaceDetailsActivity : AppCompatActivity() {

    private var binding : ActivityHappyPlaceDetailsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappyPlaceDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val registerEntity = intent.getSerializableExtra("data") as? registerEntity

        if (registerEntity!= null) {

            setSupportActionBar(binding?.toolbarHappyPlaceDetail)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = registerEntity.title

            binding?.toolbarHappyPlaceDetail?.setNavigationOnClickListener {
                onBackPressed()
            }

            binding?.ivPlaceImage?.setImageURI(Uri.parse(registerEntity.image))
            binding?.tvDescription?.text = registerEntity.description
            binding?.tvLocation?.text = registerEntity.location
        }
    }
}