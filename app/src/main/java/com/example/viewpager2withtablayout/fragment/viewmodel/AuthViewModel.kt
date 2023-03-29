package com.example.viewpager2withtablayout.fragment.viewmodel

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.viewpager2withtablayout.utils.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber


class AuthViewModel(application: Application) : AndroidViewModel(application) {

    //    private val tokenProvider = (application as App).tokenProvider
//    private val mainRepository = (application as App).repository
    private val disposableBag = CompositeDisposable()
    private var _userId: Long? = null
    val userId: Long?
        get() = _userId

    private val _callToReturn = SingleLiveEvent<Unit>()
    val callToReturn: LiveData<Unit>
        get() = _callToReturn

    private val _moveToInputCodeFragment = SingleLiveEvent<String>()
    val moveToInputCodeFragment: LiveData<String>
        get() = _moveToInputCodeFragment

    private val _timeInMilliSec = MutableLiveData<Long>()
    val timeInMilliSec: LiveData<Long>
        get() = _timeInMilliSec

    private val _loginAttempts = MutableLiveData<Int>()
    val loginAttempts: LiveData<Int>
        get() = _loginAttempts

    var loginAttemptsKey = true

    private val _registrationButtonClicked = SingleLiveEvent<Unit>()
    val registrationButtonClicked: LiveData<Unit>
        get() = _registrationButtonClicked

    private val _forgotPasswordClicked = SingleLiveEvent<Unit>()
    val forgotPasswordClicked: LiveData<Unit>
        get() = _forgotPasswordClicked

    lateinit var phoneNumber: String

    private var _countDownTimer: CountDownTimer? = null

    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
//        val credentialPOKO = CredentialPOKO(email = email, password = password)

/*        val disposable = mainRepository.loginUser(credentialPOKO)?.subscribe({ userPOKO ->
            // Получить пользователя"
            // Проверить поле result
            // Если авторизация успешна, послать событие успешной авторизации
            // Иначе послать событие с ошибкой
            authorizationUser(userPOKO, onSuccess, onError)
        }, {
            Timber.e(it, "Ошибка при авторизации пользователя")
        })

        disposable?.let { disposableBag.add(it) }*/
    }

    fun checkZeroTries() {
//        val zeroTries = getNumberOfRetries() < 1
/*        if (zeroTries) {
            val timestampOfFirstTry = mainRepository.getTimeStampOfRetries()
            val currentTime = System.currentTimeMillis()
            val result = (timestampOfFirstTry + TIMEOUT_AFTER_5_ATTEMPTS_MILLIS) - currentTime
            if (result > 0) {
                _countDownTimer?.cancel()
                countDownTimerFun(result)
            } else mainRepository.setNumberOfRetries()
        }*/
    }

    fun sendAuthSms(phoneNumber: String, onSuccess: () -> Unit, onErrorMessage: (String) -> Unit) {
        this.phoneNumber = phoneNumber
        viewModelScope.launch {
            try {
/*                val response = mainRepository.sendAuthSms(phoneNumber)
                // устанавливаем количество попыток для ввода Pin-кода
                setLoginAttempts()
                val sentRequest = System.currentTimeMillis()
                countDownTimerFun(TIMEOUT_AFTER_ATTEMPT_MILLIS)
                setTimeStampWhenSendAuthSmsRequest(sentRequest)
                // инициализируем количество попыток ввода PIN-кода
                _loginAttempts.postValue(mainRepository.getLoginAttempts())
                val firstTry = getNumberOfRetries() == 5
                if (firstTry) {
                    setTimeStampForRetries(sentRequest)
                }
                mainRepository.decreaseNumberOfRetries()
                // Если не осталось попыток, то запускаем таймаут на 6 часов
                checkZeroTries()
                _userId = response.userId
                mainRepository.setTempUserId(response.userId ?: 0L)
                if (response.success == "true") {
                    onSuccess()
                } else {
                    // отправляем ошибку по колбэку, если номер телефона не найден в базе
                    response.message?.let { onErrorMessage(it) }
                }*/
            } catch (t: Throwable) {
                Timber.e(t)
            }
        }
    }

    private fun authByReceivedSms(code: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (_userId == null || phoneNumber.isEmpty()) {
            onError("Не получен UserId или телефонный номер")
            return
        }
/*        val authSmsBodyPOKO = AuthSmsBodyPOKO(code, phoneNumber, _userId!!)

        val disposable = mainRepository.authByReceivedSms(authSmsBodyPOKO)?.subscribe({ userPoko ->
            if (userPoko.result == null) {
                return@subscribe onError(userPoko.message ?: "Неправильный код")
            }
            authorizationUser(userPoko, onSuccess) { onError(it) }
        }, {
            Timber.e(it, "Ошибка при авторизации пользователя с помощью SMS")
        })

        disposable?.let { disposableBag.add(it) }*/
    }


    /**
     * Выделил код из метода авторизации в отдельную функцию т.к. теперь две авторизации
     * по паре "логин"-"пароль" и "телефонный номер" - "код из SMS"
     **/
    private fun authorizationUser(
//        userPoko: UserPOKO,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
/*        if (userPoko.result != null) {
            when (userPoko.result) {
                SUCCESS -> {
                    // Если авторизация успешна,
                    // Сохранить пользователя в БД
                    // Послать событие успешной авторизации

                    mainRepository.setSharedBasketId(userPoko.basketId)

                    dropProductTables {
                        mainRepository.saveUserInfo(userPoko) {
                            mainRepository.getBasketListAPI({
                                mainRepository.verifyUserInfo(onSuccess)
                            }, {
                                Timber.e("Ошибка при получении корзины пользователя")
                            })
                        }
                    }
                    tokenProvider.generateAndSaveFullToken()
                }
                else -> {
                    onError(
                        userPoko.message ?: Constants.ErrorMessage.messageResolver[userPoko.result
                            ?: "error"] ?: Constants.ErrorMessage.Profile.AUTH_FAILED
                    )
                }
            }
        } else {
            onError(Constants.ErrorMessage.NETWORK_ERROR)
        }*/
    }

    private fun dropProductTables(callback: () -> Unit) {
//        val disposable = mainRepository.dropProductTables(callback = callback)
//        disposableBag.add(disposable)
    }

    fun returnToParentFragment() {
        _callToReturn.postValue(Timber.d("returnToParentFragment"))
    }

    fun countDownTimerFun(timeInSec: Long) {
        _countDownTimer = object : CountDownTimer(timeInSec, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timeInMilliSec.postValue(millisUntilFinished)
            }

            override fun onFinish() {
                _timeInMilliSec.postValue(0L)
                loginAttemptsKey = true
            }
        }.start()
    }

    fun textViewListener(
        queryFlow: Flow<String>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            queryFlow.collect {
/*                val loginAttempts = mainRepository.getLoginAttempts()
                if (it.length == 4 && loginAttempts > 0) {
                    mainRepository.decreaseLoginAttempts()
                    _loginAttempts.postValue(mainRepository.getLoginAttempts())
                    authByReceivedSms(it, onSuccess, onError)
                }*/
            }
        }
    }

    /**
     * Пришлось сделать так, т.к. не смог получить фрагмент в TabLayout фрагменте
     **/
    fun goToInputCodeFragment(phoneNumber: String) {
        _moveToInputCodeFragment.postValue(phoneNumber)
    }

    fun registrationButtonClicked() {
        _registrationButtonClicked.postValue(Unit)
    }

    fun forgotPasswordButtonClicked() {
        _forgotPasswordClicked.postValue(Unit)
    }

    private fun setTimeStampWhenSendAuthSmsRequest(timestamp: Long) {
//        mainRepository.setTimeStampWhenSendAuthSmsRequest(timestamp)
    }

    private fun setTimeStampForRetries(timestamp: Long) {
//        mainRepository.setTimeStampForRetries(timestamp)
    }

/*    private fun getTimeStampWhenAuthSmsRequestSent() =
        mainRepository.getTimeStampWhenAuthSmsRequestSent()*/

    fun checkWhenRequestWasSent(): Long {
/*        val requestWasSentMillis = getTimeStampWhenAuthSmsRequestSent()
        val currentTimeMillis = System.currentTimeMillis()

        val result = (requestWasSentMillis + TIMEOUT_AFTER_ATTEMPT_MILLIS) - currentTimeMillis
        return if (result > 0L) {
            result
        } else*/
        return 0L
    }

//    fun getNumberOfRetries(): Int = mainRepository.getNumberOfRetries()

//    fun checkNumberOfRetries(): Boolean = mainRepository.getNumberOfRetries() > 0

    fun getAlreadySentUserId() {
//        _userId = mainRepository.getTempUserId()
    }

    fun setTempPhoneNumber(phoneNumber: String) {
//        mainRepository.setTempPhoneNumber(phoneNumber)
    }

//    fun getAlreadySentPhoneNumber() = mainRepository.getTempPhoneNumber()

    fun setLoginAttempts() {
//        mainRepository.setLoginAttempts()
    }

    override fun onCleared() {
        super.onCleared()
        disposableBag.dispose()
    }

    companion object {
        const val SUCCESS: String = "success"

        const val TIMEOUT_AFTER_ATTEMPT_MILLIS = 61000L
        const val TIMEOUT_AFTER_5_ATTEMPTS_MILLIS = 21_600_000L
    }
}
