package sfedu.ictis.walkOfInterest.presentation.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import sfedu.ictis.walkOfInterest.databinding.FragmentMainCreateBinding
import sfedu.ictis.walkOfInterest.utils.ToastManager
import sfedu.ictis.walkOfInterest.presentation.generate.GenerateActivity

class MainCreateFragment : Fragment() {

    private var _binding: FragmentMainCreateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fieldCreateTrip.setOnClickListener {
            // Переход на экран генерации маршрута
            val intent = Intent(requireContext(), GenerateActivity::class.java)
            startActivity(intent)
        }

        binding.fieldCreateSpot.setOnClickListener {
            // TODO: Переход на экран создания POI
            ToastManager.show(requireContext(), "Скоро будет!")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}