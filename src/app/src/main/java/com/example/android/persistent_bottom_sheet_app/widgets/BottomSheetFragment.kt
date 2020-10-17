package com.example.android.persistent_bottom_sheet_app.widgets

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.*
import com.example.android.persistent_bottom_sheet_app.R
import com.google.android.material.bottomsheet.BottomSheetBehavior

const val BOTTOM_SHEET_FRAGMENT_TAG = "BOTTOM_SHEET_FRAGMENT_TAG"

class BottomSheetFragment : Fragment() {

    private lateinit var bottomSheetConstraintLayout: ConstraintLayout
    lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.bottom_sheet_fragment, container, false)

        bottomSheetConstraintLayout = root.findViewById(R.id.bottom_sheet_constraint_layout)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetConstraintLayout)

        return root
    }

    val isExpanded : Boolean
        get() = bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN

    inline fun <reified T: Fragment> show() {
        if (isExpanded) {
            swap<T>()
        } else {
            add<T>()
            expand()
        }
    }

    inline fun <reified T: Fragment> close() {
        if (isExpanded) {
            hide()
        }
    }

    inline fun <reified T: Fragment> add() = childFragmentManager.commit {
        add<T>(R.id.bottom_sheet_fragment_container_view, BOTTOM_SHEET_FRAGMENT_TAG)
    }

    inline fun <reified T: Fragment> replace() = childFragmentManager.commit {
        replace<T>(R.id.bottom_sheet_fragment_container_view, BOTTOM_SHEET_FRAGMENT_TAG)
    }

    inline fun <reified T: Fragment> swap() {
        val bottomSheetCallback = object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        bottomSheetBehavior.removeBottomSheetCallback(this)
                        logHidden()
                        replace<T>()
                        expand()
                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) { /* no-op */ }
        }

        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun remove() {
        childFragmentManager.findFragmentByTag(BOTTOM_SHEET_FRAGMENT_TAG)?.let {
            fragment -> childFragmentManager.commit { remove(fragment) }
        }
    }

    fun expand() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        bottomSheetBehavior.isHideable = false
    }

    fun hide() {
        val bottomSheetCallback = object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        bottomSheetBehavior.removeBottomSheetCallback(this)
                        logHidden()
                        remove()
                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) { /* no-op */ }
        }

        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun logHidden() {
        Log.i("BottomSheetBehavior", "---------------STATE_HIDDEN----------------")
    }
}