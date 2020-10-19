package com.example.android.persistent_bottom_sheet_app.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentContainerView
import com.example.android.persistent_bottom_sheet_app.R
import com.example.android.persistent_bottom_sheet_app.sheets.OneFragment
import com.example.android.persistent_bottom_sheet_app.sheets.TwoFragment
import com.example.android.persistent_bottom_sheet_app.widgets.BottomSheetFragment
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment() {

    private lateinit var bottomSheetFragmentTag: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.home_fragment, container, false)

        val homeFragmentContainerView = root.findViewById<FragmentContainerView>(R.id.home_fragment_container_view)
        bottomSheetFragmentTag = homeFragmentContainerView.tag as String

        val fragmentButtonOne = root.findViewById<MaterialButton>(R.id.fragment_one_button)
        val fragmentButtonTwo = root.findViewById<MaterialButton>(R.id.fragment_two_button)

        fragmentButtonOne.setOnClickListener {
            getBottomSheetFragment().show<OneFragment>()
        }

        fragmentButtonTwo.setOnClickListener {
            getBottomSheetFragment().show<TwoFragment>()
        }

        return root
    }

    private fun getBottomSheetFragment(): BottomSheetFragment {
        return childFragmentManager.findFragmentByTag(bottomSheetFragmentTag) as BottomSheetFragment
    }

}