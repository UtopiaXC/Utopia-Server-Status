package com.utopiaxc.serverstatus.intro

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import com.github.appintro.SlideBackgroundColorHolder
import com.github.appintro.SlidePolicy
import com.utopiaxc.serverstatus.R
import com.utopiaxc.serverstatus.databinding.FragmentPrivacyPolicyBinding


class PrivacyPolicyFragment(private var context: IntroActivity) : Fragment(), SlidePolicy,
    SlideBackgroundColorHolder {
    private var colorRes = R.color.white
    private var color = context.getColor(colorRes)
    private lateinit var binding: FragmentPrivacyPolicyBinding
    private var agree = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentPrivacyPolicyBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.privacyPolicyCheck.setOnCheckedChangeListener { _, isChecked ->
            agree = isChecked
        }
        return binding.root
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
        get() = color
    override val defaultBackgroundColorRes: Int
        get() = colorRes

    override fun setBackgroundColor(backgroundColor: Int) {
        binding.root.setBackgroundColor(backgroundColor)
    }

    override val isPolicyRespected: Boolean
        get() = agree

    override fun onUserIllegallyRequestedNextPage() {
        AlertDialog.Builder(this.activity).setTitle(R.string.sorry)
            .setMessage(R.string.not_agree_privacy_alert)
            .setPositiveButton(R.string.confirm, null)
            .create()
            .show()
    }
}