package sfedu.ictis.walkOfInterest.presentation.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import sfedu.ictis.walkOfInterest.databinding.FragmentTripDetailsBinding

class TripDetailsFragment : Fragment() {
    private var _binding: FragmentTripDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TripDetailsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tripId = arguments?.getString(ARG_TRIP_ID)
        if (tripId != null) {
            viewModel.loadTripDetails(tripId)
        }

        observeDeleted()
        setupListeners(view)
    }

    private fun observeDeleted() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deleted.collect { isDeleted ->
                if (isDeleted) parentFragmentManager.popBackStack()
            }
        }
    }

    private fun setupListeners(view: View) {
        view.setOnClickListener { }
        binding.fieldBtnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnTrash.setOnClickListener {
            viewModel.deleteTrip()
            //parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        Log.i("TripFragment", "onDestroy ${arguments?.getString(ARG_TRIP_ID)}")

        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TRIP_ID = "arg_trip_id"

        fun newInstance(tripId: String) = TripDetailsFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_TRIP_ID, tripId)
            }
        }
    }
}