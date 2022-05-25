package com.utopiaxc.serverstatus.intro

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.appintro.SlideBackgroundColorHolder
import com.github.appintro.SlidePolicy
import com.utopiaxc.serverstatus.R
import com.utopiaxc.serverstatus.databinding.FragmentPrivacyPolicyBinding
import com.utopiaxc.serverstatus.utils.ThemeUtil


class PrivacyPolicyFragment(private var mContext: IntroActivity) : Fragment(), SlidePolicy,
    SlideBackgroundColorHolder {
    private lateinit var mBinding: FragmentPrivacyPolicyBinding
    private var mAgree = false
    private var mColorDayRes = R.color.white
    private var mColorDay = mContext.getColor(mColorDayRes)
    private var mColorNightRes = R.color.night_background
    private var mColorNight = mContext.getColor(mColorNightRes)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = FragmentPrivacyPolicyBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding.privacyPolicyCheck.setOnCheckedChangeListener { _, isChecked ->
            mAgree = isChecked
        }
        return mBinding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(context: IntroActivity): PrivacyPolicyFragment {
            return PrivacyPolicyFragment(context)
        }
    }

    @Deprecated(
        "`defaultBackgroundColor` has been deprecated to support configuration changes",
        replaceWith = ReplaceWith("defaultBackgroundColorRes")
    )
    override val defaultBackgroundColor: Int
        get() {
            return if (ThemeUtil.isNightMode(mContext)) {
                mColorNight
            } else {
                mColorDay
            }
        }

    override val defaultBackgroundColorRes: Int
        get() {
            return if (ThemeUtil.isNightMode(mContext)) {
                mColorNightRes
            } else {
                mColorDayRes
            }
        }

    override fun setBackgroundColor(backgroundColor: Int) {
        mBinding.root.setBackgroundColor(backgroundColor)
    }

    override val isPolicyRespected: Boolean
        get() = mAgree

    override fun onUserIllegallyRequestedNextPage() {
        AlertDialog.Builder(this.activity).setTitle(R.string.sorry)
            .setMessage(R.string.not_agree_privacy_alert)
            .setPositiveButton(R.string.confirm, null)
            .create()
            .show()
    }
}