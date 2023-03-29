package com.example.viewpager2withtablayout.utils

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


@PublishedApi
internal fun ensureMainThread(): Unit = check(Looper.getMainLooper() == Looper.myLooper()) {
    "Expected to be called on the main thread but was " + Thread.currentThread().name
}

fun mainHandler(handler: () -> Unit) = Handler(Looper.getMainLooper()).post(handler)


object Utils {
    private const val DEFAULT_IMAGE_URL = "/img/lazy/product.png"

/*    fun loadImage(
        context: Context,
        imageUrl: String,
        imageView: ImageView,
        errorDrawable: Int = -1
    ) {
        val newImageUrl = if (imageUrl == "false") DEFAULT_IMAGE_URL else imageUrl

        if (errorDrawable != -1) {
            Glide.with(context).load(Settings.imageBaseUrl + newImageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(errorDrawable)
                .into(imageView)
        } else {
            Glide.with(context).load(Settings.imageBaseUrl + newImageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
        }
    }*/

/*    fun loadImage(
        imageUrl: String,
        imageView: ImageView,
        errorDrawable: Int = -1
    ) {
        val newImageUrl = if (imageUrl == "false") DEFAULT_IMAGE_URL else imageUrl

        if (errorDrawable != -1) {
            Glide.with(imageView).load(Settings.imageBaseUrl + newImageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(errorDrawable)
                .into(imageView)
        } else {
            Glide.with(imageView).load(Settings.imageBaseUrl + newImageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
        }
    }*/

    fun getSeparatedNumber(number: Double, isPrice: Boolean = true): String {
        val formatter = DecimalFormat.getInstance(Locale.getDefault()) as DecimalFormat
        val symbols = formatter.decimalFormatSymbols

        symbols.decimalSeparator = ','
        symbols.groupingSeparator = ' '
        formatter.maximumFractionDigits = 2

        formatter.decimalFormatSymbols = symbols
        formatter.applyPattern("###,###.##")
        formatter.negativePrefix = "- "

        val formattedNumber = formatter.format(number).run {
            val separatorIndex = indexOf(',')
            if (separatorIndex != -1 && separatorIndex == length - 2) this + "0" else this
        }

        val priceSign = if (isPrice) " \u20BD" else ""

        return formattedNumber + priceSign
    }

    fun getSeparatedNumber(number: Int, isPrice: Boolean = true): String {
        return with(DecimalFormat.getInstance() as DecimalFormat) {
            val symbols = decimalFormatSymbols
            symbols.groupingSeparator = ' '
            decimalFormatSymbols = symbols

            var priceSign = ""
            if (isPrice)
                priceSign = " \u20BD"

            format(number) + priceSign
        }
    }

    fun getSeparatedNumber(number: Long, isPrice: Boolean = true): String {
        return with(DecimalFormat.getInstance() as DecimalFormat) {
            val symbols = decimalFormatSymbols
            symbols.groupingSeparator = ' '
            decimalFormatSymbols = symbols

            var priceSign = ""
            if (isPrice)
                priceSign = " \u20BD"

            format(number) + priceSign
        }
    }

    fun discountPercent(newPrice: Double, oldPrice: Double) =
        ((1.0 - newPrice / oldPrice) * 100.0).roundToInt()


    /**
     * Shows snackbar with specified message.
     */
    fun getSnackBar(anchorView: View, message: String) =
        Snackbar.make(anchorView, message, Snackbar.LENGTH_SHORT)
            .setTextColor(anchorView.context.resources.getColor(android.R.color.white))

    fun getNoDiscountSnackbar(anchorView: View) =
        Snackbar.make(
            anchorView, "Скидка по карте не предоставляется", Snackbar.LENGTH_LONG
        )
            .setTextColor(anchorView.context.resources.getColor(android.R.color.white))

    /**
     * Стандартный диалог предупреждения/уведомления, автор не известен
     * Добавил onDismiss, для возможности обработки события "отклонения" диалога @author Денис Соловьев
     **/
/*    fun getWarningDialog(
        context: Context,
        message: String,
        onPositive: (() -> Unit)? = null,
        onNegative: (() -> Unit)? = null,
        onDismiss: (() -> Unit)? = null,
    ): ConfirmationFragment {
        val dialog = ConfirmationFragment()
            .setMessage(message)
            .setTitle(context.getString(ru.positron_it.vimos.R.string.dialog_warning_title))
            .displayNegativeButton(onNegative != null)

        onPositive?.let(dialog::setPositiveButtonListener)
        onNegative?.let(dialog::setNegativeButtonListener)
        onDismiss?.let(dialog::setDismissListener)

        return dialog
    }*/

}


/**
 * Добавляет в конец билдера заданную строку с префиксом, если строка не пуста.
 * @return тот же самый билдер
 * @author Кирилл Худяков
 */
fun StringBuilder.appendIfNotBlank(string: String?, prefix: String = ""): StringBuilder {
    if (!string.isNullOrBlank()) append("$prefix$string")
    return this
}

/**
 * Переводит дату в строку по ISO 8601
 * @return Строка формата yy-MM-dd
 */
fun Date.toIsoString(): String {
    return SimpleDateFormat("yyy-MM-dd", Locale("ru")).format(this)
}

/**
 * Переводит номер дня недели в его краткое название.
 * Например, 1 -> Пн.
 * @param day Int: номер дня неделиот 1 до 7 включительно
 * @throws IllegalArgumentException Если день недели находится вне интервала от 1 до 7
 * @author Кирилл Худяков
 */
fun dayOfWeekNumberToName(day: Int): String {
    return when (day) {
        1 -> "Пн"
        2 -> "Вт"
        3 -> "Ср"
        4 -> "Чт"
        5 -> "Пт"
        6 -> "Сб"
        7 -> "Вс"
        else -> throw IllegalArgumentException("День недели должен принимать значения от 1 до 7")
    }
}

fun dayOfWeekNumberToCalendarConstant(day: Int): Int {
    return when (day) {
        1 -> Calendar.MONDAY
        2 -> Calendar.TUESDAY
        3 -> Calendar.WEDNESDAY
        4 -> Calendar.THURSDAY
        5 -> Calendar.FRIDAY
        6 -> Calendar.SATURDAY
        7 -> Calendar.SUNDAY
        else -> throw IllegalArgumentException("День недели должен принимать значения от 1 до 7")
    }
}

/**
 * Converts ISO date to human-readable date (e.g. "19 декабря"). If conversion was not successful
 * returns null.
 * @param isoDate String: date in ISO format
 * @return String?: human-readable date or null
 * @author Kirill Khudiakov
 */
/*fun isoDateToHumanDate(isoDate: String, resources: Resources): String? {
    return try {
        val (year, month, day) =
            isoDate.substringBefore('T').split('-').map(String::toInt)
        if (month < 1 || month > 12) throw IllegalArgumentException("Invalid date")

        val monthNames = resources.getStringArray(R.array.months)
        "$day ${monthNames[month - 1]} $year"
    } catch (e: Exception) {
        null
    }
}*/

fun applyPhoneMask(rawNumber: String?): String? {
    return rawNumber
        ?.filter(Char::isDigit)
        ?.takeIf { it.length >= 10 }
        ?.takeLast(10)
        ?.run {
            "+7 (%s) %s-%s-%s".format(
                substring(0..2),
                substring(3..5),
                substring(6..7),
                substring(8..9)
            )
        }
}

fun removePhoneMask(maskedNumber: String?): String? {
    return maskedNumber?.replace("(", "")
        ?.replace(")", "")
        ?.replace("-", "")
        ?.replace(" ", "")
        ?.replace("+", "")
}

fun millisToHMS(millis: Long): Triple<Long, Long, Long> {
    val s: Long =
        TimeUnit.MILLISECONDS.toSeconds(millis) %
                TimeUnit.MINUTES.toSeconds(1)
    val m: Long =
        TimeUnit.MILLISECONDS.toMinutes(millis) %
                TimeUnit.HOURS.toMinutes(1)
    val h: Long = TimeUnit.MILLISECONDS.toHours(millis)

    return Triple(h, m, s)
}

fun haveS(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S // S версия = 12 android, API 31
}

fun haveO(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O // S версия = 8 android, API 26
}