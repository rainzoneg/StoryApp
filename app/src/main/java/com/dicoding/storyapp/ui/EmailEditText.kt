package com.dicoding.storyapp.ui

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText

class EmailEditText: AppCompatEditText {
    var validity = false
    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init(){
        addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                error = null
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(!Patterns.EMAIL_ADDRESS.matcher(s).matches()){
                    validity = false
                    error = "Invalid email format."
                }else{
                    validity = true
                }
            }
            override fun afterTextChanged(s: Editable?) {
                //Nothing
            }
        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        error = null
        isSingleLine = true
        hint = "Enter your email"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}