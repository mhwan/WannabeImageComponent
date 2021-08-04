package com.mhwan.wannabeimagecomponent.motionview.ui

import android.content.Context
import android.os.Bundle
import android.text.Selection
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mhwan.wannabeimagecomponent.R

class TextEditorFragment(val textEditorCallback: TextEditorCallback) : BottomSheetDialogFragment() {
    private val editText: EditText? by lazy { view?.findViewById(R.id.et_edit_text) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_text_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val text = arguments?.let {
            it.getString(ARG_TEXT)
        } ?: ""

        initWithTextEntity(text)
        editText?.doAfterTextChanged {
            textEditorCallback.onTextChanged(it.toString())
        }
    }

    private fun initWithTextEntity(text: String) {
        editText?.let {
            it.setText(text)
            it.post {
                Selection.setSelection(it.text, it.length())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        editText?.let {
            setEditText(true)
            it.requestFocus()
            it.post {
                context?.let { context ->
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.showSoftInput(it, 0)
                }
            }
        }
    }

    private fun setEditText(gainFocus: Boolean) {
        editText?.let {
            if (!gainFocus) {
                it.clearFocus()
                it.clearComposingText()
            }
            it.isFocusableInTouchMode = gainFocus
            it.isFocusable = gainFocus
        }
    }

    override fun dismiss() {
        super.dismiss()
        context?.let {
            view?.let { view ->
                val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        System.gc()
        Runtime.getRuntime().gc()
    }

    companion object {
        fun getInstance(text: String, textEditorCallback: TextEditorCallback): TextEditorFragment {
            return TextEditorFragment(textEditorCallback).apply {
                arguments = Bundle().apply {
                    putString(ARG_TEXT, text)
                }
            }
        }

        private const val ARG_TEXT = "arg_text_editor"
    }
}