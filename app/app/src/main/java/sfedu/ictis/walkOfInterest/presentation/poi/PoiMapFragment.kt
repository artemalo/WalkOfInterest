package sfedu.ictis.walkOfInterest.presentation.poi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.Marker
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.databinding.FragmentPoiMapBinding
import sfedu.ictis.walkOfInterest.presentation.BaseFragment
import sfedu.ictis.walkOfInterest.utils.calculateColorByCategory

class PoiMapFragment : BaseFragment<FragmentPoiMapBinding>() {
    private val lat by lazy { requireArguments().getDouble(ARG_LAT) }
    private val lon by lazy { requireArguments().getDouble(ARG_LON) }
    private val poiName by lazy { requireArguments().getString(ARG_NAME) }
    private val categoryId by lazy { requireArguments().getInt(ARG_CATEGORY_ID) }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPoiMapBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Configuration.getInstance().load(
            requireContext().applicationContext,
            PreferenceManager.getDefaultSharedPreferences(requireContext().applicationContext)
        )

        binding.fieldBtnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        setupMap()
    }

    private fun setupMap() {
        val map = binding.mapView
        map.setMultiTouchControls(true)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

        val point = GeoPoint(lat, lon)
        map.controller.setZoom(17.0)
        map.controller.setCenter(point)

        val marker = Marker(map).apply {
            position = point
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_spot_on)
            title = poiName

            val newIcon = icon?.constantState?.newDrawable()?.mutate()
            newIcon?.setTint(calculateColorByCategory(categoryId))
            icon = newIcon
        }
        map.overlays.add(marker)
        map.invalidate()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    companion object {
        private const val ARG_LAT = "arg_lat"
        private const val ARG_LON = "arg_lon"
        private const val ARG_NAME = "arg_name"
        private const val ARG_CATEGORY_ID = "arg_category_id"

        fun newInstance(lat: Double, lon: Double, name: String?, catId: Int): PoiMapFragment =
            PoiMapFragment().apply {
                arguments = Bundle().apply {
                    putDouble(ARG_LAT, lat)
                    putDouble(ARG_LON, lon)
                    putString(ARG_NAME, name)
                    putInt(ARG_CATEGORY_ID, catId)
                }
            }
    }
}
