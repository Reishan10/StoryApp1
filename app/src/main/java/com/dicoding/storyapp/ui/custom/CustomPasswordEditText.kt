package com.dicoding.storyapp.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.R

class CustomPasswordEditText : AppCompatEditText {
    private lateinit var passwordIcon: Drawable
    private var characterCount = 0

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize()
    }

    private fun initialize() {
        passwordIcon =
            ContextCompat.getDrawable(context, R.drawable.ic_lock) as Drawable
        showPasswordIcon()
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                characterCount = s.length
                if (!s.isNullOrEmpty() && characterCount < 8) error =
                    context.getString(R.string.label_validation_password)
            }

            override fun afterTextChanged(edt: Editable?) {}
        })
    }

    private fun showPasswordIcon() {
        setButtonDrawables(startOfTheText = passwordIcon)
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        context.apply {
            setTextColor(ContextCompat.getColor(this, R.color.pink_400))
            setHintTextColor(ContextCompat.getColor(this, R.color.pink_400))
            background = ContextCompat.getDrawable(this, R.drawable.form_input)
        }
        maxLines = 1
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        transformationMethod = PasswordTransformationMethod.getInstance()
    }
}
