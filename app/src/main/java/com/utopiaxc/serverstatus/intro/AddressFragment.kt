package com.utopiaxc.serverstatus.intro

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.github.appintro.SlideBackgroundColorHolder
import com.github.appintro.SlidePolicy
import com.utopiaxc.serverstatus.R
import com.utopiaxc.serverstatus.databinding.FragmentAddressBinding
import com.utopiaxc.serverstatus.utils.ThemeUtil
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * API地址设置Fragment
 * <p>继承Fragment，实现欢迎页政策接口与颜色渐变接口
 *
 * @author UtopiaXC
 * @since 2022-05-22 22:35:08
 */
open class AddressFragment(private var mContext: IntroActivity) : Fragment(), SlidePolicy,
    SlideBackgroundColorHolder {
    private var mAddressIsSet = false
    private var mColorDayRes = R.color.white
    private var mMessageHandler = AddressFragmentHandler(mContext.mainLooper)
    private var mColorDay = mContext.getColor(mColorDayRes)
    private var mColorNightRes = R.color.night_background
    private var mColorNight = mContext.getColor(mColorNightRes)
    private lateinit var mBinding: FragmentAddressBinding
    private lateinit var mAddress: String

    /**
     * Fragment创建
     *
     * @author UtopiaXC
     * @since 2022-05-22 22:38:14
     * @param savedInstanceState 传入参数
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = FragmentAddressBinding.inflate(layoutInflater)
    }

    /**
     * Fragment视图创建
     *
     * @author UtopiaXC
     * @since 2022-05-22 22:38:51
     * @param inflater 传入参数
     * @param container 传入参数
     * @param savedInstanceState 传入参数
     * @return
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //显示提示
        mBinding.buttonAddressHelp.setOnClickListener {
            AlertDialog.Builder(mContext).setTitle(R.string.tips)
                .setMessage(R.string.address_help)
                .setPositiveButton(R.string.confirm, null)
                .create()
                .show()
        }
        //完成API地址输入
        mBinding.buttonSubmitAddress.setOnClickListener {
            mAddress = mBinding.editTextAddress.text.toString()
            //检查地址格式
            val pattern = "^https://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$"
            val r: Pattern = Pattern.compile(pattern)
            val m: Matcher = r.matcher(mAddress)
            if (!m.matches()) {
                AlertDialog.Builder(mContext).setTitle(R.string.warning)
                    .setMessage(R.string.address_format_wrong)
                    .setPositiveButton(R.string.confirm, null)
                    .create()
                    .show()
                return@setOnClickListener
            }
            mBinding.buttonSubmitAddress.setText(R.string.wait)
            mBinding.buttonSubmitAddress.isEnabled = false

            //隐藏软键盘
            val imm: InputMethodManager = requireView().context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)

            //格式检查通过，检查API
            Thread(CheckUrl()).start()
        }
        //当地址发生改变时重置设置状态
        mBinding.editTextAddress.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                mBinding.buttonSubmitAddress.setText(R.string.confirm)
                mBinding.buttonSubmitAddress.setBackgroundColor(getColorPrimary())
                mBinding.buttonSubmitAddress.isEnabled = true
                mAddressIsSet = false
                mAddress = ""
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
                val editor = sharedPreferences.edit()
                editor.remove("address")
                editor.remove("first_start")
                editor.apply()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
        return mBinding.root
    }

    /**
     * 获取主题颜色
     *
     * @author UtopiaXC
     * @since 2022-05-22 22:42:09
     * @return kotlin.Int
     */
    open fun getColorPrimary(): Int {
        val typedValue = TypedValue()
        mContext.theme
            .resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)
        return typedValue.data
    }

    /**
     * API可用性线程
     * <p>通过Runnable接口实现
     *
     * @author UtopiaXC
     * @since 2022-05-22 22:43:27
     */
    private inner class CheckUrl : Runnable {
        /**
         * API可用性线程方法
         *
         * @author UtopiaXC
         * @since 2022-05-22 22:44:02
         */
        override fun run() {
            //构建API地址
            if (mAddress.last() != '/') {
                mAddress += '/'
            }
            mAddress += "json/stats.json"

            //构建API请求
            val connect: Connection =
                Jsoup.connect(mAddress).header("Accept", "*/*")
                    .header("Content-Type", "application/xml;charset=UTF-8")
                    .ignoreContentType(true)
            try {
                //解析请求，如果服务器数量小于1则发送失败消息
                val document = connect.get()
                //处理JSON
                val servers = JSONArray.parseArray(
                    JSONObject.parseObject(document.body().text()).getString("servers")
                )
                if (servers.size < 1) {
                    mAddressIsSet = false
                    val message = Message()
                    message.what = ADDRESS_CHECK_FLAG
                    mMessageHandler.sendMessage(message)
                    return
                }
            } catch (e: Exception) {
                //当请求发生异常发送失败消息
                e.printStackTrace()
                mAddressIsSet = false
                val message = Message()
                message.what = ADDRESS_CHECK_FLAG
                mMessageHandler.sendMessage(message)
                return
            }
            //发送成功消息
            mAddressIsSet = true
            val message = Message()
            message.what = ADDRESS_CHECK_FLAG
            mMessageHandler.sendMessage(message)
        }
    }


    /**
     * API检查信息句柄
     *
     * @author UtopiaXC
     * @since 2022-05-22 22:46:03
     */
    inner class AddressFragmentHandler(looper: Looper) : Handler(looper) {
        /**
         * 消息处理
         *
         * @author UtopiaXC
         * @since 2022-05-22 22:48:04
         * @param msg 消息
         */
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == Companion.ADDRESS_CHECK_FLAG) {
                if (mAddressIsSet) {
                    //如果检查成功则通过按钮显示并设置偏好值
                    mBinding.buttonSubmitAddress.setText(R.string.succeed)
                    mBinding.buttonSubmitAddress.isEnabled = false
                    mBinding.buttonSubmitAddress.setBackgroundColor(mContext.getColor(R.color.succeed))
                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
                    val editor = sharedPreferences.edit()
                    editor.putString("address", mAddress)
                    editor.putBoolean("first_start", false)
                    editor.apply()
                } else {
                    //如果地址检查失败则提示
                    mBinding.buttonSubmitAddress.setText(R.string.confirm)
                    mBinding.buttonSubmitAddress.isEnabled = true
                    AlertDialog.Builder(mContext).setTitle(R.string.warning)
                        .setMessage(R.string.address_invalid)
                        .setPositiveButton(R.string.confirm, null)
                        .create()
                        .show()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(context: IntroActivity): AddressFragment {
            return AddressFragment(context)
        }

        protected const val ADDRESS_CHECK_FLAG: Int = 215098490
    }

    override val isPolicyRespected: Boolean
        get() = mAddressIsSet

    override fun onUserIllegallyRequestedNextPage() {
        AlertDialog.Builder(this.activity).setTitle(R.string.sorry)
            .setMessage(R.string.address_set_alert)
            .setPositiveButton(R.string.confirm, null)
            .create()
            .show()
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
}