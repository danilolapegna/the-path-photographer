package com.pathphotographer.app.ui

import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.pathphotographer.app.R
import com.pathphotographer.app.ui.activity.MainActivity
import kotlinx.android.synthetic.main.activity_main.*

fun MainActivity.getFragmentInContainer(): Fragment? {
    return supportFragmentManager.findFragmentById(R.id.fragment_container)
}

fun MainActivity.displayToast(stringRes: Int, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, stringRes, length).show()
}

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

fun MainActivity.showSnackbar(
    message: String?,
    colorRes: Int,
    actionLabel: String? = null,
    listener: View.OnClickListener? = null
) {
    rootCoordinator?.let {
        val snackbar: Snackbar = Snackbar.make(it, message ?: "", Snackbar.LENGTH_LONG)
        snackbar.setActionTextColor(ContextCompat.getColor(this, android.R.color.white))
        snackbar.view.setBackgroundColor(ContextCompat.getColor(this, colorRes))
        if (!actionLabel.isNullOrEmpty() && listener != null) {
            snackbar.setAction(actionLabel, listener)
        }
        snackbar.show()
    }
}