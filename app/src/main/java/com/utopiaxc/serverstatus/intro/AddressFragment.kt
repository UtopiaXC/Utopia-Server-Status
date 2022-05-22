package com.utopiaxc.serverstatus.intro

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.github.appintro.SlideBackgroundColorHolder
import com.github.appintro.SlidePolicy
import com.utopiaxc.serverstatus.R
import com.utopiaxc.serverstatus.databinding.FragmentAddressBinding
import com.utopiaxc.serverstatus.services.ServerStatusUpdateService
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.util.regex.Matcher
import java.util.regex.Pattern


open class AddressFragment(private var context: IntroActivity) : Fragment(), SlidePolicy,
    SlideBackgroundColorHolder {
    private var addressIsSet = false
    private var colorRes = R.color.white
    private var messager = AddressFragmentHandler(context.mainLooper)
    private var color = context.getColor(colorRes)
    private lateinit var binding: FragmentAddressBinding
    private lateinit var address: String
    private lateinit var jsonContent: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentAddressBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.buttonAddressHelp.setOnClickListener {
            AlertDialog.Builder(context).setTitle(R.string.tips)
                .setMessage(R.string.address_help)
                .setPositiveButton(R.string.confirm, null)
                .create()
                .show()
        }
        binding.buttonSubmitAddress.setOnClickListener {
            address = binding.editTextAddress.text.toString()
            val pattern = "^https://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$"
            val r: Pattern = Pattern.compile(pattern)
            val m: Matcher = r.matcher(address)
            if (!m.matches()) {
                AlertDialog.Builder(context).setTitle(R.string.warning)
                    .setMessage(R.string.address_format_wrong)
                    .setPositiveButton(R.string.confirm, null)
                    .create()
                    .show()
                return@setOnClickListener
            }
            binding.buttonSubmitAddress.setText(R.string.wait)
            binding.buttonSubmitAddress.isEnabled = false
            val imm: InputMethodManager = requireView().context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
            Thread(CheckUrl()).start()
        }
        binding.editTextAddress.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.buttonSubmitAddress.setText(R.string.confirm)
                binding.buttonSubmitAddress.setBackgroundColor(getColorPrimary())
                binding.buttonSubmitAddress.isEnabled = true
                addressIsSet = false
                address = ""
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                val editor = sharedPreferences.edit()
                editor.remove("address")
                editor.remove("first_start")
                editor.apply()
                val service = Intent(context, ServerStatusUpdateService::class.java)
                context.stopService(service)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
        return binding.root
    }

    open fun getColorPrimary(): Int {
        val typedValue = TypedValue()
        context.theme
            .resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)
        return typedValue.data
    }

    private inner class CheckUrl : Runnable {
        override fun run() {
            if (address.last() != '/') {
                address += '/'
            }
            address += "json/stats.json"
            val connect: Connection =
                Jsoup.connect(address).header("Accept", "*/*")
                    .header("Content-Type", "application/xml;charset=UTF-8")
                    .ignoreContentType(true)
            try {
                val document = connect.get()
                jsonContent = JSON.parseObject(document.body().text())
                val servers = JSON.parseArray(jsonContent["servers"].toString())
                for (server in servers) {
                    println(server)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                addressIsSet = false
                val message = Message()
                message.what = ADDRESS_CHECK_FLAG
                messager.sendMessage(message)
                return
            }
            addressIsSet = true
            val message = Message()
            message.what = ADDRESS_CHECK_FLAG
            messager.sendMessage(message)
        }
    }

    inner class ServerSetReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

        }
    }


    inner class AddressFragmentHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == Companion.ADDRESS_CHECK_FLAG) {
                if (addressIsSet) {

                    val receiver = ServerSetReceiver()
                    val intentFilter = IntentFilter()
                    intentFilter.addAction("com.utopiaxc.serverstatus.SERVER_STATUS_UPDATED")
                    context.registerReceiver(
                        receiver,
                        intentFilter,
                        "com.utopiaxc.receiver.receivebroadcast",
                        null
                    )

                    val broadcast = Intent("com.utopiaxc.serverstatus.SERVER_STATUS_UPDATED")
                    context.sendBroadcast(broadcast)

                    binding.buttonSubmitAddress.setText(R.string.succeed)
                    binding.buttonSubmitAddress.isEnabled = false
                    binding.buttonSubmitAddress.setBackgroundColor(context.getColor(R.color.succeed))
                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                    val editor = sharedPreferences.edit()
                    editor.putString("address", address)
                    editor.putBoolean("first_start", false)
                    editor.apply()
                    val service = Intent(context, ServerStatusUpdateService::class.java)
                    context.startService(service)
                } else {
                    binding.buttonSubmitAddress.setText(R.string.confirm)
                    binding.buttonSubmitAddress.isEnabled = true
                    AlertDialog.Builder(context).setTitle(R.string.warning)
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
        get() = addressIsSet

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
        get() = color
    override val defaultBackgroundColorRes: Int
        get() = colorRes

    override fun setBackgroundColor(backgroundColor: Int) {
        binding.root.setBackgroundColor(backgroundColor)
    }
}