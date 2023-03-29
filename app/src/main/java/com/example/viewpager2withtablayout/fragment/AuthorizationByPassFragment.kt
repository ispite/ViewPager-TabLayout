package com.example.viewpager2withtablayout.fragment

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.viewpager2withtablayout.R
import com.example.viewpager2withtablayout.databinding.FragmentAuthByPasswordBinding
import com.example.viewpager2withtablayout.fragment.viewmodel.AuthViewModel
import com.example.viewpager2withtablayout.utils.Utils
import timber.log.Timber

class AuthorizationByPassFragment : Fragment() {

    private lateinit var binding: FragmentAuthByPasswordBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)

        binding = FragmentAuthByPasswordBinding.inflate(inflater, container, false)
        setupAuthByPassword()

        return binding.root
    }

    private fun setupAuthByPassword() {
        binding.authForgotPassword.setOnClickListener {
            viewModel.forgotPasswordButtonClicked()
        }
        binding.authSingUpButton.setOnClickListener {
            viewModel.registrationButtonClicked()
        }
        binding.authLogInButton.setOnClickListener {
            onLogInButtonClicked()
        }
        binding.authPasswordText.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) {
                onLogInButtonClicked()
                dismissKeyboard()
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun validateAuthorization(): Boolean {
        return with(binding) { validateFields(authEmailText, authPasswordText) }
    }

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

    private fun onLogInButtonClicked() {
        if (!validateAuthorization()) return

        dismissKeyboard()
        deactivateButton(binding)
        viewModel.loginUser(
            binding.authEmailText.text.toString(),
            binding.authPasswordText.text.toString(),
            {
                Timber.i("Успешная авторизация")
                activateButton(binding)
                viewModel.returnToParentFragment()
            },
            { errorMessage ->
                Utils.getSnackBar(binding.authLogInButton, errorMessage).show()
                activateButton(binding)
            }
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
}