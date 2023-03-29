package com.example.viewpager2withtablayout.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import java.text.DecimalFormat
import java.util.*

fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}

fun Double.toSeparatedNumber(isPrice: Boolean = true): String {
    val formatter = DecimalFormat.getInstance(Locale.getDefault()) as DecimalFormat
    val symbols = formatter.decimalFormatSymbols

    symbols.decimalSeparator = ','
    symbols.groupingSeparator = ' '
    formatter.maximumFractionDigits = 2

    formatter.decimalFormatSymbols = symbols
    formatter.applyPattern("###,###.##")
    formatter.negativePrefix = "- "

    val formattedNumber = formatter.format(this).run {
        val separatorIndex = indexOf(',')
        if (separatorIndex != -1 && separatorIndex == length - 2) this + "0" else this
    }

    val priceSign = if (isPrice) " \u20BD" else ""

    return formattedNumber + priceSign
}

fun Int.toSeparatedNumber(isPrice: Boolean = true): String {
    return with(DecimalFormat.getInstance() as DecimalFormat) {
        val symbols = decimalFormatSymbols
        symbols.groupingSeparator = ' '
        decimalFormatSymbols = symbols

        var priceSign = ""
        if (isPrice)
            priceSign = " \u20BD"

        format(this) + priceSign
    }
}

fun Long.toSeparatedNumber(isPrice: Boolean = true): String {
    return with(DecimalFormat.getInstance() as DecimalFormat) {
        val symbols = decimalFormatSymbols
        symbols.groupingSeparator = ' '
        decimalFormatSymbols = symbols

        var priceSign = ""
        if (isPrice)
            priceSign = " \u20BD"

        format(this) + priceSign
    }
}

/**
 * Converts pixel to dp
 **/
val Int.pxToDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

/**
 * Converts dp to pixel
 **/
val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun <T : Fragment> T.withArguments(action: Bundle.() -> Unit): T {
    return apply {
        val args = Bundle().apply(action)
        arguments = args
    }
}

fun EditText.textChangedFlow(): Flow<String> {
    return callbackFlow<String> {
        val textChangedListener = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                trySendBlocking(charSequence?.toString().orEmpty())
            }

            override fun afterTextChanged(p0: Editable?) {}
        }
        this@textChangedFlow.addTextChangedListener(textChangedListener)
        awaitClose {
            Timber.d("awaitClose ")
            this@textChangedFlow.removeTextChangedListener(textChangedListener)
        }
    }
}

fun PopupMenu.popupMenuItemClickFlow(): Flow<MenuItem> {
    return callbackFlow {
        val popupMenuChangedListener =
            PopupMenu.OnMenuItemClickListener { menuItem ->
                trySendBlocking(menuItem)
                true
            }
        this@popupMenuItemClickFlow.setOnMenuItemClickListener(popupMenuChangedListener)
        awaitClose {
            Timber.d("awaitClose radioGroup")
            this@popupMenuItemClickFlow.setOnMenuItemClickListener(null)
        }
    }
}

fun EditText.inputNumber() {
    this@inputNumber.background = null
}

// Extensions from article
// https://proandroiddev.com/how-to-detect-if-the-android-keyboard-is-open-269b255a90f5

fun Activity.getRootView(): View {
    return findViewById<View>(android.R.id.content)
}

fun Context.convertDpToPx(dp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        this.resources.displayMetrics
    )
}

fun Activity.isKeyboardOpen(): Boolean {
    val visibleBounds = Rect()
    this.getRootView().getWindowVisibleDisplayFrame(visibleBounds)
    val heightDiff = getRootView().height - visibleBounds.height()
    val marginOfError = Math.round(this.convertDpToPx(50F))
    return heightDiff > marginOfError
}

fun Activity.isKeyboardOpenHeightDiff(): Int {
    val visibleBounds = Rect()
    this.getRootView().getWindowVisibleDisplayFrame(visibleBounds)
    val heightDiff = getRootView().height - visibleBounds.height()
    val marginOfError = Math.round(this.convertDpToPx(50F))
    return if (heightDiff > marginOfError) heightDiff else 0
}

fun Activity.isKeyboardClosed(): Boolean {
    return !this.isKeyboardOpen()
}

val Number.dpToPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

/**
 * Добавил extension'ы для сокрытия клавиатуры, взял отсюда https://stackoverflow.com/a/45857155
 **/
fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager =
        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}