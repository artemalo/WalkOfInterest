package sfedu.ictis.walkOfInterest.presentation.poi.add.form

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.BottomsheetSubcategoryPickBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainPickCategory

class SubcategoryPickBottomSheet : BottomSheetDialogFragment() {
    override fun getTheme(): Int = R.style.AppBottomSheetDialog

    private var _binding: BottomsheetSubcategoryPickBinding? = null
    private val binding get() = _binding!!

    private val parentViewModel: AddPoiFormViewModel by viewModel(
        ownerProducer = { requireParentFragment() }
    )

    private lateinit var adapter: SubcategoryPickAdapter
    private var category: DomainPickCategory? = null

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

        val cat = readCategoryArg()
        category = cat
        binding.title.text = cat?.name ?: "Подкатегории"

        adapter = SubcategoryPickAdapter(
            onToggle = { sub -> parentViewModel.onSubcategoryToggled(sub) }
        )

        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter

        binding.btnDone.setOnClickListener { dismiss() }

        observeSelected()
    }

    private fun observeSelected() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                parentViewModel.uiState
                    .map { it.selectedSubcategoryIds }
                    .distinctUntilChanged()
                    .collect { selectedIds ->
                        renderList(selectedIds)
                    }
            }
        }
    }

    private fun renderList(selectedIds: Set<Int>) {
        val cat = category ?: return
        adapter.submitList(
            cat.subcategories.map { sub ->
                SubcategoryPickAdapter.Item(
                    sub = sub,
                    isSelected = selectedIds.contains(sub.id)
                )
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Suppress("DEPRECATION")
    private fun readCategoryArg(): DomainPickCategory? {
        val args = arguments ?: return null
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            args.getParcelable(ARG_CATEGORY, DomainPickCategory::class.java)
        } else {
            args.getParcelable(ARG_CATEGORY)
        }
    }

    companion object {
        const val TAG = "SubcategoryPickBottomSheet"
        private const val ARG_CATEGORY = "arg_category"

        fun newInstance(category: DomainPickCategory): SubcategoryPickBottomSheet =
            SubcategoryPickBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_CATEGORY, category)
                }
            }
    }
}
