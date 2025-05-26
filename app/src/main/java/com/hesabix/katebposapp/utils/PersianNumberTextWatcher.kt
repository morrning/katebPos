package com.hesabix.katebposapp.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class PersianNumberTextWatcher(private val editText: EditText) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable?) {
        if (s != null) {
            val text = s.toString()
            if (text.isNotEmpty()) {
                val persianText = PersianNumberConverter.convertToPersian(text)
                if (text != persianText) {
                    editText.removeTextChangedListener(this)
                    editText.setText(persianText)
                    editText.setSelection(persianText.length)
                    editText.addTextChangedListener(this)
                }
            }
        }
    }
} 