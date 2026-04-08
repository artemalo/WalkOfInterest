package sfedu.ictis.walkOfInterest.presentation.main

import androidx.fragment.app.Fragment
import sfedu.ictis.walkOfInterest.R

class TripsFragment : Fragment(R.layout.fragment_trips) {

    private val viewModel: TripsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.loadTrips()

        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                // обновляешь RecyclerView
            }
        }
    }
}