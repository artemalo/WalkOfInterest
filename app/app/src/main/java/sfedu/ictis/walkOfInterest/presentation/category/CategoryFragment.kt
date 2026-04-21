package sfedu.ictis.walkOfInterest.presentation.category

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.FragmentCategoryBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainCategory
import sfedu.ictis.walkOfInterest.domain.model.DomainSubCategory
import sfedu.ictis.walkOfInterest.presentation.categories.CategoriesViewModel
import sfedu.ictis.walkOfInterest.utils.formatMinutes

class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoryViewModel by viewModel()
    private val categoriesViewModel: CategoriesViewModel by activityViewModels()
    private lateinit var adapter: SubcategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val category = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_CATEGORY, DomainCategory::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_CATEGORY)
        }

        category?.let { viewModel.initCategory(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeState()
    }

    private fun setupRecyclerView() {
        adapter = SubcategoryAdapter(
            onSeeAllClick = { subcategory ->
                openSubcategoryFragment(subcategory)
            },
            onPoiClick = { subcategoryId, poiId ->
                viewModel.onPoiClicked(subcategoryId, poiId)
            }
        )
        binding.itemList.layoutManager = LinearLayoutManager(requireContext())
        binding.itemList.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.fieldBtnEdit.setOnClickListener {
            viewModel.toggleEditMode()
        }

        binding.btnOk.setOnClickListener {
            viewModel.saveChanges { category, time, allPoisCount, selectedCount ->
                categoriesViewModel.updateCategoryPois(category, time, allPoisCount, selectedCount)
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.category?.let { cat ->
                    binding.categoryName.text = cat.name
                    adapter.submitList(cat.subcategories)

                    binding.allPois.text = state.allPoisCount.toString()
                    binding.countSelected.text = state.selectedCount.toString()
                }

                if (state.isEditMode) {
                    binding.btnBack.visibility = View.GONE
                    binding.btnOk.visibility = View.VISIBLE
                    binding.btnEditText.text = "Отмена"
                    binding.btnEdit.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.object_red)
                    )
                } else {
                    binding.btnBack.visibility = View.VISIBLE
                    binding.btnOk.visibility = View.GONE
                    binding.btnEditText.text = "Редактировать"
                    binding.btnEdit.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.object_orange)
                    )
                }

                when {
                    state.isTimeLoading -> {
                        binding.time.text = "..."
                    }
                    state.isEditMode -> {
                        binding.time.text = "-"
                    }
                    state.time != null -> {
                        binding.time.text = formatMinutes(state.time)
                    }
                    else -> {
                        binding.time.text = "-"
                    }
                }
            }
        }
    }

    private fun openSubcategoryFragment(subcategory: DomainSubCategory) {
        // TODO: SubcategoryFragment
//        val fragment = SubcategoryFragment.newInstance(subcategory)
//        parentFragmentManager.beginTransaction()
//            .replace(R.id., fragment)
//            .addToBackStack(null)
//            .commit()
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CATEGORY = "arg_category"

        fun newInstance(category: DomainCategory): CategoryFragment {
            return CategoryFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_CATEGORY, category)
                }
            }
        }
    }
}