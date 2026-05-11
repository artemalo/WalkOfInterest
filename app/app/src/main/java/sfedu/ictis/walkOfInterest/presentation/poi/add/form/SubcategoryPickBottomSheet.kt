package sfedu.ictis.walkOfInterest.presentation.poi.add.form

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.BottomsheetSubcategoryPickBinding

class SubcategoryPickBottomSheet : BottomSheetDialogFragment() {
    override fun getTheme(): Int = R.style.AppBottomSheetDialog

    private var _binding: BottomsheetSubcategoryPickBinding? = null
    private val binding get() = _binding!!

    private val parentViewModel: AddPoiFormViewModel by viewModel(
        ownerProducer = { requireParentFragment() }
    )

    private val pickViewModel: SubcategoryPickViewModel by viewModel()

    private lateinit var adapter: SubcategoryPickAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetSubcategoryPickBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryId = arguments?.getInt(ARG_CATEGORY_ID, -1) ?: -1
        val categoryName = arguments?.getString(ARG_CATEGORY_NAME) ?: "Подкатегории"

        binding.title.text = categoryName

        adapter = SubcategoryPickAdapter(
            onToggle = { sub -> parentViewModel.onSubcategoryToggled(sub) }
        )

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter       = this@SubcategoryPickBottomSheet.adapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(rv, dx, dy)
                    if (dy <= 0) return
                    val lm  = rv.layoutManager as LinearLayoutManager
                    val last = lm.findLastVisibleItemPosition()
                    val total = lm.itemCount

                    if (total > 0 && last >= total - 3) {
                        pickViewModel.loadNextPage()
                    }
                }
            })
        }

        binding.btnDone.setOnClickListener { dismiss() }

        setupSearch()
        observeState()

        if (categoryId != -1) {
            pickViewModel.init(categoryId)
        }
    }

    private fun setupSearch() {
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun afterTextChanged(s: Editable?) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                pickViewModel.onSearch(s?.toString().orEmpty())
            }
        })
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    pickViewModel.state,
                    parentViewModel.uiState
                ) { pickState, formState ->
                    Pair(pickState, formState.selectedSubcategoryIds)
                }
                    .distinctUntilChanged()
                    .collect { (pickState, selectedIds) ->
                        renderList(pickState, selectedIds)
                    }
            }
        }
    }

    private fun renderList(state: SubcategoryPickUiState, selectedIds: Set<Int>) {
        binding.progressBar.visibility =
            if (state.isLoading && state.items.isEmpty()) View.VISIBLE else View.GONE

        val isEmpty = !state.isLoading && state.items.isEmpty() && state.error == null
        binding.emptyText.visibility = if (isEmpty) View.VISIBLE else View.GONE

        adapter.submitList(
            state.items.map { sub ->
                SubcategoryPickAdapter.Item(sub = sub, isSelected = selectedIds.contains(sub.id))
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SubcategoryPickBottomSheet"
        private const val ARG_CATEGORY_ID   = "arg_category_id"
        private const val ARG_CATEGORY_NAME = "arg_category_name"

        fun newInstance(categoryId: Int, categoryName: String): SubcategoryPickBottomSheet =
            SubcategoryPickBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt(ARG_CATEGORY_ID, categoryId)
                    putString(ARG_CATEGORY_NAME, categoryName)
                }
            }
    }
}
