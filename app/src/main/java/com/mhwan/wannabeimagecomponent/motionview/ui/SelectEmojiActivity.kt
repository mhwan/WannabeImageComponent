package com.mhwan.wannabeimagecomponent.motionview.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.mhwan.wannabeimagecomponent.databinding.ActivitySelectEmojiBinding
import com.mhwan.wannabeimagecomponent.motionview.utils.EmojiUtils

class SelectEmojiActivity: AppCompatActivity(), ChangeEmojiCallback {
    private lateinit var binding: ActivitySelectEmojiBinding
    private var selectEmojiId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectEmojiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.toolbar.tvTitle.text = "이모티콘 추가"
        binding.toolbar.ibClose.setOnClickListener {
            setResultWithFinish(true)
        }
        binding.toolbar.ibOkay.setOnClickListener {
            setResultWithFinish()
        }
        binding.rvSelectEmoji.layoutManager = GridLayoutManager(baseContext, 4)
        binding.rvSelectEmoji.adapter = SelectEmojiAdapter(
            EmojiUtils.getEmojiList(),
            baseContext,
            this
        )
    }

    private fun setResultWithFinish(canceled: Boolean = false) {
        if (canceled || selectEmojiId < 0) {
            setResult(RESULT_CANCELED)
        } else {
            val emojiResourceId = EmojiUtils.getEmojiList()[selectEmojiId]
            setResult(
                RESULT_OK_EMOJI_STICKER,
                Intent().apply {
                    putExtra(EXTRA_EMOJI_ID, emojiResourceId)
                }
            )
        }
        finish()
    }

    companion object {
        const val EXTRA_EMOJI_ID = "extra_sticker_id"
        const val RESULT_OK_EMOJI_STICKER = 0x127
    }

    override fun onChangeSelectEmojiIndex(emojiIdx: Int) {
        selectEmojiId = emojiIdx
    }
}