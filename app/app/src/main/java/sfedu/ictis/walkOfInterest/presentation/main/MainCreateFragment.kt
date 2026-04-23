package sfedu.ictis.walkOfInterest.presentation.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import sfedu.ictis.walkOfInterest.databinding.FragmentMainCreateBinding
import sfedu.ictis.walkOfInterest.presentation.BaseFragment
import sfedu.ictis.walkOfInterest.utils.ToastManager
import sfedu.ictis.walkOfInterest.presentation.generate.GenerateActivity

class MainCreateFragment : BaseFragment<FragmentMainCreateBinding>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainCreateBinding {
        return FragmentMainCreateBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("MainCreateFragment", "onViewCreated")

        setupListeners()
    }

    private fun setupListeners() {
        binding.fieldCreateTrip.setOnClickListener {
            val intent = Intent(requireContext(), GenerateActivity::class.java)
            startActivity(intent)
        }

        binding.fieldCreateSpot.setOnClickListener {
            // TODO: Переход на экран создания POI
            ToastManager.show(requireContext(), "Скоро будет!")
        }
    }
}