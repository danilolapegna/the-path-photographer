package com.komoot.test.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.komoot.test.R
import com.komoot.test.ui.fragment.MainFragment
import com.komoot.test.ui.switchFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null)
            switchFragment(
                MainFragment.newInstance(),
                R.id.fragment_container,
                false
            )
    }
}
