package com.rizfadh.dicostory.ui.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.rizfadh.dicostory.R

class EmailEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val email = p0.toString()
                if(!isValid(email) && email.isNotEmpty()) {
                    error = context.getString(R.string.invalid_email)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun isValid(email: CharSequence): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}