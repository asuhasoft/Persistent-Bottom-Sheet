# Persistent-Bottom-Sheet

[prototype design] persistent bottom sheet deisgn to show fragments dynamically 

## Design Implementation

`bottom_sheet_layout.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".widgets.BottomSheetFragment"
    app:layout_behavior="@string/appbar_scrolling_view_behavior">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:id="@+id/bottom_sheet_constraint_layout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:behavior_peekHeight="60dp"
        app:behavior_hideable="false"
        app:behavior_fitToContents="true"
        app:layout_behavior="com.google.android.material.bottomsheet.BottomSheetBehavior">

        <androidx.fragment.app.FragmentContainerView
            android:id="@+id/bottom_sheet_fragment_container_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>

    </androidx.constraintlayout.widget.ConstraintLayout>

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

`BottomSheetFragment.kt`
```kotlin
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
```

## Consuming the Bottom Sheet 

`home_fragment.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".screens.HomeFragment">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <com.google.android.material.button.MaterialButton
            android:id="@+id/fragment_one_button"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_margin="8dp"
            app:layout_constraintTop_toTopOf="parent"
            android:elevation="4dp"
            android:text="Bottom Sheet Fragment One" />

        <com.google.android.material.button.MaterialButton
            android:id="@+id/fragment_two_button"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_margin="8dp"
            app:layout_constraintTop_toBottomOf="@+id/fragment_one_button"
            android:elevation="4dp"
            android:text="Bottom Sheet Fragment Two" />

    </androidx.constraintlayout.widget.ConstraintLayout>

    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/home_fragment_container_view"
        android:name="com.example.android.persistent_bottom_sheet_app.widgets.BottomSheetFragment"
        android:tag="bottom_sheet_fragment_tag"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

`HomeFragment.kt`

```kotlin
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
```

## Sample App
<img src='https://user-images.githubusercontent.com/19615296/96354933-0ed3dc80-1091-11eb-9e5f-403fbbbed91b.png' width='35%' /><img src='https://user-images.githubusercontent.com/19615296/96354981-9faab800-1091-11eb-9ece-b736b824ab14.png' width='35%' />
