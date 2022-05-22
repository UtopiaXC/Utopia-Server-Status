package com.utopiaxc.serverstatus.intro

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment
import com.utopiaxc.serverstatus.MainActivity
import com.utopiaxc.serverstatus.R
import com.utopiaxc.serverstatus.activities.SettingsActivity

class IntroActivity : AppIntro2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isWizardMode = true
        isColorTransitionsEnabled = true
        supportActionBar?.hide()
        addSlide(
            AppIntroFragment.createInstance(
                getString(R.string.welcome),
                getString(R.string.rights),
                R.drawable.logo,
                R.color.purple_500,
            )
        )
        addSlide(AddressFragment.newInstance(this))
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}