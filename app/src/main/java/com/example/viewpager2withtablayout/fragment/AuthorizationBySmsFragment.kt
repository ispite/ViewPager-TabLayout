package com.example.viewpager2withtablayout.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.viewpager2withtablayout.R
import com.example.viewpager2withtablayout.databinding.FragmentAuthByPasswordBinding
import com.example.viewpager2withtablayout.databinding.FragmentAuthBySmsCodeBinding
import com.example.viewpager2withtablayout.fragment.viewmodel.AuthViewModel
import com.example.viewpager2withtablayout.fragment.viewmodel.AuthViewModel.Companion.TIMEOUT_AFTER_ATTEMPT_MILLIS
import com.example.viewpager2withtablayout.utils.Utils
import com.example.viewpager2withtablayout.utils.millisToHMS
import com.example.viewpager2withtablayout.utils.removePhoneMask
import com.example.viewpager2withtablayout.utils.withArguments

class AuthorizationBySmsFragment : Fragment() {

    private lateinit var binding: FragmentAuthBySmsCodeBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
        binding = FragmentAuthBySmsCodeBinding.inflate(inflater, container, false)

        setupTextEditListener()

        setupViews()

        return binding.root
    }

    private fun setupViews() {
        binding.authSendCodeButton.setOnClickListener {
            onSendSmsClicked()
        }
        binding.authSingUpButton.setOnClickListener {
            viewModel.registrationButtonClicked()
        }
        // Если уже запрашивал код, то ожидай
        // Ожидай минуту если только что отправил
        val result = viewModel.checkWhenRequestWasSent()
        if (result in 1..TIMEOUT_AFTER_ATTEMPT_MILLIS) {
            viewModel.countDownTimerFun(result)
        }
        // Ожидай 6 часов если истратил все попытки
        viewModel.checkZeroTries()
        viewModel.timeInMilliSec.observe(viewLifecycleOwner) {
            val hms = millisToHMS(it)
            when {
                (it == 0L) -> {
                    binding.authSendCodeButton.text = getString(R.string.receive_code)
                }
                (hms.first > 0L) -> {
                    binding.authSendCodeButton.text =
                        getString(
                            R.string.receive_code_with_h_m_s,
                            hms.first,
                            hms.second,
                            hms.third
                        )
                }
                (hms.second > 0L) -> {
                    binding.authSendCodeButton.text =
                        getString(R.string.receive_code_with_m_s, hms.second, hms.third)
                }
                (hms.third > 0L) -> {
                    binding.authSendCodeButton.text =
                        getString(R.string.receive_code_with_s, hms.third)

                }
            }
        }
    }

    private fun onSendSmsClicked() {
        if (!validatePhoneNumber()) return
        if (!viewModel.loginAttemptsKey) {
            Utils.getSnackBar(
                binding.authSendCodeButton,
                "Дождитесь окончания таймера, чтобы попробовать вновь"
            ).show()
            return
        }

        dismissKeyboard()

        val phoneNumber = binding.authPhoneText.text.toString()

        val timeInMillis = viewModel.timeInMilliSec.value ?: 0L
        val alreadySent = timeInMillis > 0L
//        val checkResultOfRetries = viewModel.checkNumberOfRetries()
/*        if (alreadySent || checkResultOfRetries.not()) {
            viewModel.getAlreadySentUserId()
            val alreadySentPhoneNumber = viewModel.getAlreadySentPhoneNumber()

            when {
                phoneNumber != alreadySentPhoneNumber -> {
                    Utils.getSnackBar(
                        binding.authSendCodeButton,
                        "Чтобы получить код на новый номер, пожалуйста дождитесь окончания таймера"
                    ).show()
                }
                viewModel.userId == null || viewModel.userId == 0L -> {
                    Utils.getSnackBar(
                        binding.authSendCodeButton,
                        "К данному номеру не привязан аккаунт, проверьте правильность введённых данных или введите другой номер."
                    ).show()
                }
                else -> {
                    viewModel.goToInputCodeFragment(phoneNumber)
                }
            }

        } else {
            // сохраняем телефонный номер, если пользователь закроет приложение
            viewModel.setTempPhoneNumber(phoneNumber)

            viewModel.sendAuthSms(phoneNumber, {
                viewModel.goToInputCodeFragment(phoneNumber)
            }, { onErrorMessage ->
                // выводим ошибку, если номер телефона не найден в базе
                Utils.getSnackBar(binding.authSendCodeButton, onErrorMessage).show()
            })
        }*/
    }

    private fun validatePhoneNumber(): Boolean {
        val phoneField = binding.authPhoneText
        val phone = phoneField.text.toString()
        val phoneWithoutMask = removePhoneMask(phone) ?: ""
        return if (android.util.Patterns.PHONE.matcher(phoneWithoutMask)
                .matches() && (phoneWithoutMask.length == 11)
        ) {
            true
        } else {
            focusOnEditText(phoneField)
            false
        }
    }

    private fun goToFragment(fragmentClass: Class<out Fragment>) {
//        (parentFragment as? MainProfileFragment)?.goToChildFragment(fragmentClass.newInstance())
    }

/*    private fun validateAuthorization(bindingAuthByPass: FragmentAuthByPasswordBinding): Boolean {
        return with(bindingAuthByPass) { validateFields(authEmailText, authPasswordText) }
    }*/

    /**
     * Checks if each of specified EditText is not empty.
     */
    private fun validateFields(vararg fields: EditText): Boolean {
        for (field in fields) {
            if (field.text.isBlank()) {
                focusOnEditText(field)
                return false
            }
        }
        return true
    }

    /**
     * Focuses on specified EditText and shows keyboard.
     */
    private fun focusOnEditText(editText: EditText) {
        editText.requestFocus()

        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.showSoftInput(
            editText, InputMethodManager.SHOW_IMPLICIT
        )
    }

    private fun dismissKeyboard() {
        val focusedView = activity?.currentFocus

        focusedView?.let {
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            inputMethodManager.hideSoftInputFromWindow(focusedView.windowToken, 0)
        }
    }

    private fun deactivateButton(bindingAuthByPass: FragmentAuthByPasswordBinding) {
        bindingAuthByPass.authProgressBar.visibility = View.VISIBLE
        bindingAuthByPass.authLogInButton.isEnabled = false
        bindingAuthByPass.authLogInButton.text = ""
    }

    private fun activateButton(bindingAuthByPass: FragmentAuthByPasswordBinding) {
        bindingAuthByPass.authProgressBar.visibility = View.GONE
        bindingAuthByPass.authLogInButton.isEnabled = true
        bindingAuthByPass.authLogInButton.text = getString(R.string.log_in)
    }

    private fun setupTextEditListener() {

        // Show "+7 (" in the beginning of editText.
        binding.authPhoneText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.authPhoneText.text.toString().isEmpty()) {
                binding.authPhoneText.setText(" ")
                binding.authPhoneText.text?.delete(4, 5)
            }
        }

        val mask = "+7 ("
        binding.authPhoneText.doOnTextChanged { text, _, _, _ ->
            with(binding.authPhoneText) {
                timber.log.Timber.i("Phone number changed: $text")
                timber.log.Timber.i("Input length = ${length()}, mask length = ${mask.length}")
                if (length() < mask.length) post {
                    setText(mask)
                    setSelection(length())
                }
            }
        }

        val errorMask = "+7 (("
        binding.authPhoneText.doAfterTextChanged { text ->
            if (text.toString() == errorMask) {
                text?.delete(4, 5)
            }
        }
    }

    companion object {
        private const val KEY_TAB_NUMBER = "TAB_NUMBER"

        fun newInstance(tabNumber: Int): AuthorizationBySmsFragment {
            return AuthorizationBySmsFragment().withArguments {
                putInt(KEY_TAB_NUMBER, tabNumber)
            }
        }
    }
}