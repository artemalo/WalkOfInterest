package sfedu.ictis.walkOfInterest.presentation.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.FragmentTripDetailsBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainTrip
import sfedu.ictis.walkOfInterest.presentation.BaseFragment
import sfedu.ictis.walkOfInterest.utils.ToastManager
import sfedu.ictis.walkOfInterest.utils.formatMinutes
import sfedu.ictis.walkOfInterest.utils.openPoiFragment

class TripDetailsFragment : BaseFragment<FragmentTripDetailsBinding>() {
    private val viewModel: TripDetailsViewModel by viewModel()
    private lateinit var adapter: TripRoutePointAdapter

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTripDetailsBinding {
        return FragmentTripDetailsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners(view)
        observeState()
        observeDeleted()

        val tripId = arguments?.getString(ARG_TRIP_ID)
        if (tripId != null) {
            viewModel.loadTripDetails(tripId)
        } else {
            ToastManager.show(requireContext(), "Не указан идентификатор маршрута")
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
        adapter = TripRoutePointAdapter { point ->
            requireActivity().openPoiFragment(
                poiId = point.id,
                container = requireActivity().findViewById(R.id.fragment_container)
            )
        }

        binding.itemList.layoutManager = LinearLayoutManager(requireContext())
        binding.itemList.adapter = adapter
    }

    private fun setupListeners(view: View) {
        view.setOnClickListener { }

        binding.fieldBtnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.fieldBtnTrash.setOnClickListener {
            viewModel.deleteTrip()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.notFound) {
                        ToastManager.show(requireContext(), "Маршрут не найден")
                        parentFragmentManager.popBackStack()
                        return@collect
                    }

                    state.trip?.let { renderTrip(it) }
                }
            }
        }
    }

    private fun observeDeleted() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteState.collect { state ->
                    when (state) {
                        is DeleteState.Success -> parentFragmentManager.popBackStack()
                        is DeleteState.Error ->
                            ToastManager.show(requireContext(), "Ошибка удаления маршрута")
                        is DeleteState.Idle -> Unit
                    }
                }
            }
        }
    }

    private fun renderTrip(trip: DomainTrip) {
        binding.textFrom.text = trip.addressFrom
        binding.textTo.text = trip.addressTo

        binding.timeTotal.text = trip.totalPois.toString()

        val time = trip.bestRouteTime ?: trip.userSelectedTime
        binding.timeCurrent.text = formatMinutes(time)

        adapter.submitList(trip.selectedPois.sortedBy { it.order })
    }

//    private fun openPoiFragment(point: RoutePoint) {
//        val fragment = PoiFragment.newInstance(point.id)
//        requireActivity().supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, fragment)
//            .addToBackStack(null)
//            .commit()
//    }

    companion object {
        private const val ARG_TRIP_ID = "arg_trip_id"

        fun newInstance(tripId: String) = TripDetailsFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_TRIP_ID, tripId)
            }
        }
    }
}