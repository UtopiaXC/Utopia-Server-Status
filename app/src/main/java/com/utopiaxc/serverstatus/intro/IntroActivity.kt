package com.utopiaxc.serverstatus.intro

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment
import com.utopiaxc.serverstatus.R

class IntroActivity : AppIntro2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isWizardMode=true
        isColorTransitionsEnabled = true
        supportActionBar?.hide()
        addSlide(AppIntroFragment.createInstance(
            getString(R.string.welcome),
            getString(R.string.rights),
            R.drawable.logo,
            R.color.purple_500,
        ))
        addSlide(AddressFragment.newInstance(this))
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        finish()
    }
}