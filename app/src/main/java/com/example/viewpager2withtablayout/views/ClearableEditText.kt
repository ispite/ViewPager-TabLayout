package com.example.viewpager2withtablayout.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.viewpager2withtablayout.utils.TextWatcherAdapter

open class ClearableEditText : AppCompatEditText, OnTouchListener, View.OnFocusChangeListener,
    TextWatcherAdapter.TextWatcherListener {

    private val location: Location? = Location.RIGHT

    private var clearDrawable: Drawable? = null
    private var listener: Listener? = null

    private var touchListener: OnTouchListener? = null
    private var focusChangeListener: OnFocusChangeListener? = null

    private val displayedDrawable: Drawable?
        get() = location?.index?.let(compoundDrawables::get)

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    override fun setOnTouchListener(l: OnTouchListener) {
        this.touchListener = l
    }

    override fun setOnFocusChangeListener(f: OnFocusChangeListener) {
        this.focusChangeListener = f
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (displayedDrawable != null) {
            val x = event.x.toInt()
            val y = event.y.toInt()
            val left = if (location == Location.LEFT)
                0 else width - paddingRight - (clearDrawable?.intrinsicWidth ?: 0)
            val right = if (location == Location.LEFT)
                paddingLeft + (clearDrawable?.intrinsicWidth ?: 0) else width
            val tappedX = x in left..right && y >= 0 && y <= bottom - top
            if (tappedX) {
                if (event.action == MotionEvent.ACTION_UP) {
                    setText("")
                    listener?.didClearText()
                }
                return true
            }
        }
        return touchListener?.onTouch(v, event) ?: false
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        if (hasFocus) {
            setClearIconVisible(text?.isNotEmpty() != true)
        } else {
            setClearIconVisible(false)
        }
        focusChangeListener?.onFocusChange(v, hasFocus)
    }

    override fun onTextChanged(view: EditText, text: String) {
        if (isFocused) {
            setClearIconVisible(text.isNotEmpty())
        }
    }

    override fun setCompoundDrawables(
        left: Drawable?,
        top: Drawable?,
        right: Drawable?,
        bottom: Drawable?
    ) {
        super.setCompoundDrawables(left, top, right, bottom)
        initIcon()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        super.setOnTouchListener(this)
        super.setOnFocusChangeListener(this)
        addTextChangedListener(
            TextWatcherAdapter(
                this,
                this
            )
        )
        initIcon()
        setClearIconVisible(false)
    }

    private fun initIcon() {
        val drawable = location?.index?.let(compoundDrawables::get)
            ?: ContextCompat.getDrawable(context, android.R.drawable.presence_offline)
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

        clearDrawable = drawable

        val min = paddingTop + (drawable?.intrinsicHeight ?: 0) + paddingBottom
        if (suggestedMinimumHeight < min) {
            minimumHeight = min
        }
    }

    private fun setClearIconVisible(visible: Boolean) {
        val cd = compoundDrawables
        val displayed = displayedDrawable
        val wasVisible = displayed != null
        if (visible != wasVisible) {
            val x = if (visible) clearDrawable else null
            super.setCompoundDrawables(
                if (location == Location.LEFT) x else cd[0],
                cd[1],
                if (location == Location.RIGHT) x else cd[2],
                cd[3]
            )
        }
    }

    enum class Location(internal val index: Int) {
        LEFT(0), RIGHT(2)
    }

    interface Listener {
        fun didClearText()
    }
}
