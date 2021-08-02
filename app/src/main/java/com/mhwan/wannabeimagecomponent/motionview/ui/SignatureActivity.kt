package com.mhwan.wannabeimagecomponent.motionview.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.github.dhaval2404.imagepicker.ImagePicker
import com.mhwan.wannabeimagecomponent.databinding.ActivitySignatureBinding
import com.mhwan.wannabeimagecomponent.motionview.ui.adapter.FontsAdapter
import com.mhwan.wannabeimagecomponent.motionview.utils.FontUtils
import com.mhwan.wannabeimagecomponent.motionview.utils.ImageUtils
import com.mhwan.wannabeimagecomponent.motionview.viewmodel.Font
import com.mhwan.wannabeimagecomponent.motionview.viewmodel.Layer
import com.mhwan.wannabeimagecomponent.motionview.viewmodel.TextLayer
import com.mhwan.wannabeimagecomponent.motionview.widget.MotionView
import com.mhwan.wannabeimagecomponent.motionview.widget.entity.ImageEntity
import com.mhwan.wannabeimagecomponent.motionview.widget.entity.MotionEntity
import com.mhwan.wannabeimagecomponent.motionview.widget.entity.TextEntity

class SignatureActivity : AppCompatActivity(), TextEditorCallback {
    private lateinit var binding: ActivitySignatureBinding
    private val fontUtils: FontUtils by lazy {
        FontUtils(
            resources
        )
    }
    private val emojiResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ::onEmojiStickerResult)
    private val galleryPickerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ::onGalleryPickResult)

    private val motionViewCallback = object: MotionView.MotionViewCallback {
        override fun onEntitySelected(entity: MotionEntity?) {
            if (entity != null) {
                binding.btnRemoveSticker.visibility = View.VISIBLE
                if (entity is TextEntity) {
                    binding.rootEditTextPanel.visibility = View.VISIBLE
                } else {
                    binding.rootEditTextPanel.visibility = View.GONE
                }
            } else {
                binding.btnRemoveSticker.visibility = View.GONE
                binding.rootEditTextPanel.visibility = View.GONE
            }
        }

        override fun onEntityDoubleTap(entity: MotionEntity) {
            changeTextFromTextEntity()
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignatureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        binding.motionView.setMotionViewCallback(motionViewCallback)
    }

    private fun initView() {
        binding.toolbar.tvTitle.text = "서명 추가"
        binding.btnFontSizeDown.setOnClickListener {
            sizeDownTextEntity()
        }
        binding.btnFontSizeUp.setOnClickListener {
            sizeUpTextEntity()
        }
        binding.btnFontColorChange.setOnClickListener {
            changeColorTextEntity()
        }
        binding.btnFontChange.setOnClickListener {
            changeFontTextEntity()
        }
        binding.btnEditText.setOnClickListener {
            changeTextFromTextEntity()
        }
        binding.btnRemoveSticker.setOnClickListener {
            binding.motionView.deletedSelectedEntity()
            binding.btnRemoveSticker.visibility = View.GONE
            binding.rootEditTextPanel.visibility = View.GONE
        }
        binding.btnAddImageSticker.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .maxResultSize(512, 512)
                .createIntent {
                    galleryPickerResult.launch(it)
                }
        }
        binding.btnAddEmojiSticker.setOnClickListener {
            addEmojiSticker()
        }
        binding.btnAddTextSticker.setOnClickListener {
            addTextSticker()
        }
    }

    private fun sizeUpTextEntity() {
        val textEntity = currentTextEntity()
        textEntity?.let {
            it.layer.font.increaseSize(TextLayer.Limits.FONT_SIZE_STEP)
            it.updateEntity()
            binding.motionView.invalidate()
        }
    }

    private fun sizeDownTextEntity() {
        val textEntity = currentTextEntity()
        textEntity?.let {
            it.layer.font.decreaseSize(TextLayer.Limits.FONT_SIZE_STEP)
            it.updateEntity()
            binding.motionView.invalidate()
        }
    }

    private fun changeColorTextEntity() {
        val textEntity = currentTextEntity() ?: return
        val initialColor = textEntity.layer.font.color
        ColorPickerDialogBuilder
            .with(this)
            .setTitle("글자색 변경")
            .initialColor(initialColor)
            .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
            .density(8) // magic number
            .setPositiveButton("확인") { _, selectedColor, _ ->
                val textEntity = currentTextEntity()
                textEntity?.let {
                    it.layer.font.color = selectedColor
                    it.updateEntity()
                    binding.motionView.invalidate()
                }
            }
            .setNegativeButton("취소") { _, _ -> }
            .build()
            .show()
    }

    private fun changeFontTextEntity() {
        val fonts = fontUtils.fontNames
        val fontsAdapter = FontsAdapter(this, fonts, fontUtils)
        AlertDialog.Builder(this)
            .setTitle("폰트 변경")
            .setAdapter(
                fontsAdapter,
            ) { _, which ->
                val textEntity = currentTextEntity()
                textEntity?.let {
                    it.layer.font.typeface = fonts[which]
                    it.updateEntity()
                    binding.motionView.invalidate()
                }
            }
            .show()
    }

    private fun changeTextFromTextEntity() {
        val textEntity = currentTextEntity()
        textEntity?.let {
            val bottomSheetDialogFragment = TextEditorFragment.getInstance(it.layer.text, this)
            bottomSheetDialogFragment.show(supportFragmentManager, "")
        }
    }

    private fun currentTextEntity(): TextEntity? {
        return if (binding.motionView.selectedEntity is TextEntity) {
            binding.motionView.selectedEntity as TextEntity
        } else {
            null
        }
    }

    private fun addTextSticker() {
        TextEntity(
            createTextLayer(),
            binding.motionView.width,
            binding.motionView.height,
            fontUtils
        ).apply {
            moveCenterTo(absoluteCenter().apply {
                y *= 0.5f
            })
        }.also {
            binding.motionView.addEntityAndPosition(it)
        }
        binding.motionView.invalidate()
        changeTextFromTextEntity()
    }

    private fun createTextLayer(): TextLayer {
        val textLayer = TextLayer()
        textLayer.font = Font().apply {
            color = TextLayer.Limits.INITIAL_FONT_COLOR
            size = TextLayer.Limits.INITIAL_FONT_SIZE
            typeface = fontUtils.defaultFontName
        }
        textLayer.text = "안녕하세요!"
        return textLayer
    }

    private fun addEmojiSticker() {
        val intent = Intent(this, SelectEmojiActivity::class.java)
        emojiResult.launch(intent)
    }

    private fun onEmojiStickerResult(activityResult: ActivityResult) {
        if (activityResult.resultCode == SelectEmojiActivity.RESULT_OK_EMOJI_STICKER) {
            activityResult.data?.let {
                val stickerId = it.getIntExtra(SelectEmojiActivity.EXTRA_EMOJI_ID, 0)
                if (stickerId != 0) {
                    val bitmap = BitmapFactory.decodeResource(resources, stickerId)
                    addSticker(bitmap)
                }
            }
        }
    }

    private fun onGalleryPickResult(activityResult: ActivityResult) {
        when (activityResult.resultCode) {
            Activity.RESULT_OK -> {
                activityResult.data?.data?.let { uri ->
                    ImageUtils.getBitmapFromUri(this, uri)?.let {
                        addSticker(it)
                    }
                }
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(activityResult.data), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addSticker(bitmap: Bitmap) {
        binding.motionView.post{
            val layer = Layer()

            ImageEntity(
                layer,
                bitmap,
                binding.motionView.width,
                binding.motionView.height)
                .also {
                    binding.motionView.addEntityAndPosition(it)
                }
        }
    }

    override fun onTextChanged(text: String) {
        val textEntity = currentTextEntity()
        textEntity?.let {
            val textLayer = it.layer
            if (textLayer.text != text) {
                textLayer.text = text
                it.updateEntity()
                binding.motionView.invalidate()
            }
        }
    }
}