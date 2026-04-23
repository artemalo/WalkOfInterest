package sfedu.ictis.walkOfInterest.presentation.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import sfedu.ictis.walkOfInterest.databinding.FragmentTripDetailsBinding
import sfedu.ictis.walkOfInterest.presentation.BaseFragment
import sfedu.ictis.walkOfInterest.utils.ToastManager

class TripDetailsFragment : BaseFragment<FragmentTripDetailsBinding>() {
    private val viewModel: TripDetailsViewModel by viewModel()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTripDetailsBinding {
        return FragmentTripDetailsBinding.inflate(inflater, container, false)
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
            viewModel.deleteState.collect { state ->
                when (state) {
                    is DeleteState.Success -> parentFragmentManager.popBackStack()
                    is DeleteState.Error -> ToastManager.show(requireContext(), "Ошибка удаления маршрута")
                    is DeleteState.Idle -> Unit
                }
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
        }
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