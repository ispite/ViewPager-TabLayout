package com.example.viewpager2withtablayout.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.viewpager2withtablayout.R
import com.example.viewpager2withtablayout.adapter.ViewPagerAdapter
import com.example.viewpager2withtablayout.databinding.FragmentContainerBinding
import com.google.android.material.tabs.TabLayoutMediator

class ContainerFragment : Fragment(R.layout.fragment_container) {

    private lateinit var binding: FragmentContainerBinding

    private val animalsArray = arrayOf(
        "Cat",
        "Dog",
        "Bird",
        "Электронная почта",
        "Номер телефона"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContainerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = animalsArray[position]
        }.attach()
    }
}