package org.damienoreilly.threeutils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment

class ThreeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_three)

        if (savedInstanceState == null) {
            val host = NavHostFragment.create(R.navigation.my3_navigation)
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.my3_fragment_container, host)
                    .setPrimaryNavigationFragment(host)
                    .commit()
        }

    }

}
