package sfedu.ictis.walkOfInterest.utils

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import sfedu.ictis.walkOfInterest.R
import sfedu.ictis.walkOfInterest.presentation.poi.PoiFragment


object PoiNavigator {
    fun open(
        activity: FragmentActivity,
        container: View,
        fragment: PoiFragment,
        containerId: Int = R.id.fragment_container
    ) {
        container.visibility = View.VISIBLE

        activity.supportFragmentManager.beginTransaction()
            .replace(containerId, fragment)
            .addToBackStack(null)
            .commit()

        attachAutoHide(activity.supportFragmentManager, container)
    }

    private fun attachAutoHide(fm: FragmentManager, container: View) {
        val attached = container.getTag(R.id.fragment_container) == true
        if (attached) return
        container.setTag(R.id.fragment_container, true)

        fm.addOnBackStackChangedListener {
            if (fm.backStackEntryCount == 0) {
                container.visibility = View.GONE
            }
        }
    }
}

fun FragmentActivity.openPoiFragment(poiId: Long, container: View) {
    PoiNavigator.open(this, container, PoiFragment.newInstance(poiId))
}