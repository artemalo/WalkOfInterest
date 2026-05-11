package sfedu.ictis.walkOfInterest.presentation.poi.add

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.BottomsheetSimilarPoisBinding
import sfedu.ictis.walkOfInterest.domain.model.DomainPoiNearby

class SimilarPoisBottomSheet : BottomSheetDialogFragment() {
    override fun getTheme(): Int = R.style.AppBottomSheetDialog

    private var _binding: BottomsheetSimilarPoisBinding? = null
    private val binding get() = _binding!!

    interface Listener {
        fun onOpenSimilar(poi: DomainPoiNearby)
        fun onAddInfoToSimilar(poi: DomainPoiNearby)
        fun onAlwaysCreateNew()
        fun onSimilarSheetDismissed()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetSimilarPoisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listener = parentFragment as? Listener ?: activity as? Listener

        val pois: List<DomainPoiNearby> = readPoisArg()

        val adapter = SimilarPoisAdapter(
            onOpenClicked = {
                listener?.onOpenSimilar(it)
                dismissAllowingStateLoss()
            },
            onAddInfoClicked = {
                listener?.onAddInfoToSimilar(it)
                dismissAllowingStateLoss()
            }
        )

        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter
        adapter.submitList(pois)

        binding.btnCreateAnyway.setOnClickListener {
            listener?.onAlwaysCreateNew()
            dismissAllowingStateLoss()
        }
        binding.btnClose.setOnClickListener { dismiss() }
    }

    override fun onCancel(dialog: android.content.DialogInterface) {
        super.onCancel(dialog)
        (parentFragment as? Listener ?: activity as? Listener)?.onSimilarSheetDismissed()
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        (parentFragment as? Listener ?: activity as? Listener)?.onSimilarSheetDismissed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Suppress("DEPRECATION", "UNCHECKED_CAST")
    private fun readPoisArg(): List<DomainPoiNearby> {
        val args = arguments ?: return emptyList()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            args.getParcelableArrayList(ARG_POIS, DomainPoiNearby::class.java) ?: emptyList()
        } else {
            args.getParcelableArrayList(ARG_POIS) ?: emptyList()
        }
    }

    companion object {
        const val TAG = "SimilarPoisBottomSheet"
        private const val ARG_POIS = "arg_pois"

        fun newInstance(pois: List<DomainPoiNearby>): SimilarPoisBottomSheet =
            SimilarPoisBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_POIS, ArrayList(pois))
                }
            }
    }
}