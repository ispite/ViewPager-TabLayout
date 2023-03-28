package com.example.viewpager2withtablayout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.viewpager2withtablayout.databinding.ActivityMainBinding
import com.example.viewpager2withtablayout.fragment.ContainerFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val fragment = ContainerFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.mainContainer, fragment)
            .commit()
    }
}