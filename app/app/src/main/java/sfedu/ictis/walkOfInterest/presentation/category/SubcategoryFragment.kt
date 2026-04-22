package sfedu.ictis.walkOfInterest.presentation.category

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import sfedu.ictis.walkOfInterest.databinding.FragmentSubcategoryBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainSubCategory

class SubcategoryFragment : Fragment() {
    private var _binding: FragmentSubcategoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoryViewModel by activityViewModel()
    private lateinit var adapter: PoiAdapter

    private var subcategoryId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubcategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val subcategory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_SUBCATEGORY, DomainSubCategory::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_SUBCATEGORY)
        }

        binding.name.text = subcategory?.name ?: "Подкатегория"

        setupRecyclerView()

        subcategory?.let {
            subcategoryId = it.id
            binding.name.text = it.name
            setupRecyclerView()
            observeViewModel()
        }

        binding.fieldBtnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
        adapter = PoiAdapter(isVerticalList = true) { poi ->
            viewModel.onPoiClicked(subcategoryId, poi.id)
        }

        binding.itemList.layoutManager = LinearLayoutManager(requireContext())
        binding.itemList.adapter = adapter
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



    override fun onDestroyView() {
        Log.i("SubcategoryFragment", "onDestroyView")
        super.onDestroyView()
        _binding = null
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