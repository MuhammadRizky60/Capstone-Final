package com.example.agronect.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.agronect.R
import com.example.agronect.data.response.Animal

class DetailHistoryActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityDetailHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_history)

//        binding.btnBack.setOnClickListener {
//            finish()
//        }

        val data = intent.getParcelableExtra<Animal>("Data")
        val tvNama: TextView = findViewById(R.id.tv_namehistory)
        val tvDeskripsi: TextView = findViewById(R.id.tv_descricpion)
        val imgPhoto: ImageView = findViewById(R.id.iv_img)
        Log.d("DetailActivity", "Data: $data")

        tvNama.text = data?.name.toString()
        tvDeskripsi.text = "${data?.description.toString()}"
        data?.photo?.let { imgPhoto.setImageResource(it) }

        val actionBar = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}