package com.utopiaxc.serverstatus.intro

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment
import com.utopiaxc.serverstatus.MainActivity
import com.utopiaxc.serverstatus.R

/**
 * 欢迎框架
 * <p>由于该框架由Kotlin开发，因此本Activity使用Kotlin开发
 *
 * @author UtopiaXC
 * @since 2022-05-22 22:30:06
 */
class IntroActivity : AppIntro2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置允许颜色渐变与引导模式
        isWizardMode = true
        isColorTransitionsEnabled = true
        supportActionBar?.hide()

        //添加首页
        addSlide(
            AppIntroFragment.createInstance(
                getString(R.string.welcome),
                getString(R.string.rights),
                R.drawable.logo,
                R.color.purple_500,
            )
        )

        //添加API地址设置页
        addSlide(AddressFragment.newInstance(this))
    }

    /**
     * 跳过监听
     * <p>当用户跳过时触发
     *
     * @author UtopiaXC
     * @since 2022-05-22 22:33:39
     * @param currentFragment
     * @return
     */
    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        finish()
    }

    /**
     * 完成监听
     * <p>当用户完成引导触发
     *
     * @author UtopiaXC
     * @since 2022-05-22 22:34:04
     * @param currentFragment
     * @return
     */
    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}