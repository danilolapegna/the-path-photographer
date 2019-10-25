package com.komoot.test.ui

import androidx.fragment.app.Fragment
import com.komoot.test.ui.activity.MainActivity

fun MainActivity.switchFragment(
    newFragment: Fragment?,
    container: Int,
    addToBackStack: Boolean = false
) {
    if (newFragment == null || newFragment.isAdded) return

    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(container, newFragment, newFragment.javaClass.name)

    if (addToBackStack) transaction.addToBackStack(null)
    transaction.commit()
}