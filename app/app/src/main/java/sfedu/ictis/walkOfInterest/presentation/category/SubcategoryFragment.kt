package sfedu.ictis.walkOfInterest.presentation.category

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.FragmentSubcategoryBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainPoi
import sfedu.ictis.walkOfInterest.domain.model.DomainSubCategory
import sfedu.ictis.walkOfInterest.presentation.BaseFragment
import sfedu.ictis.walkOfInterest.presentation.poi.PoiFragment

class SubcategoryFragment : BaseFragment<FragmentSubcategoryBinding>() {
    private val viewModel: CategoryViewModel by activityViewModel()
    private lateinit var adapter: PoiAdapter
    private var layoutManagerState: android.os.Parcelable? = null

    private var subcategoryId: Int = -1

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSubcategoryBinding {
        return FragmentSubcategoryBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val subcategory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_SUBCATEGORY, DomainSubCategory::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_SUBCATEGORY)
        }

        if (subcategory != null) {
            subcategoryId = subcategory.id
            binding.name.text = subcategory.name
        } else {
            binding.name.text = "Подкатегория"
        }

        setupRecyclerView()
        observeViewModel()

        binding.fieldBtnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
        if (!::adapter.isInitialized) {
            adapter = PoiAdapter(isVerticalList = true) { poi ->
                if (viewModel.uiState.value.isEditMode) {
                    viewModel.onPoiClicked(subcategoryId, poi.id)
                } else {
                    openPoi(poi)
                }
            }
        }

        val layoutManager = LinearLayoutManager(requireContext())
        if (layoutManagerState != null) {
            layoutManager.onRestoreInstanceState(layoutManagerState)
        }
        binding.itemList.layoutManager = layoutManager
        binding.itemList.adapter = adapter
    }

    override fun onPause() {
        super.onPause()
        (binding.itemList.layoutManager as? LinearLayoutManager)?.let {
            layoutManagerState = it.onSaveInstanceState()
        }
    }

    private fun openPoi(poi: DomainPoi) {
        val fragment = PoiFragment.newInstance(poi)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                val updatedSubcategory = state.category?.subcategories?.find { it.id == subcategoryId }

                updatedSubcategory?.pois?.let { pois ->
                    adapter.submitList(pois)
                }
            }
        }
    }



    companion object {
        private const val ARG_SUBCATEGORY = "arg_subcategory"

        fun newInstance(subcategory: DomainSubCategory): SubcategoryFragment {
            return SubcategoryFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SUBCATEGORY, subcategory)
                }
            }
        }
    }
}