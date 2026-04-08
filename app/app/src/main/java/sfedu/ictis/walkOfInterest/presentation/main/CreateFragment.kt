package sfedu.ictis.walkOfInterest.presentation.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import sfedu.ictis.walkOfInterest.R

class CreateFragment : Fragment(R.layout.fragment_main_create) {
    private val viewModel: CreateViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.loadTrips()

        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {

            }
        }
    }
}