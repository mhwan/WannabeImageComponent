package com.mhwan.wannabeimagecomponent.motionview.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mhwan.wannabeimagecomponent.databinding.ItemSelectEmojiBinding

class SelectEmojiAdapter(
    private val emojiList: List<Int>,
    private val context: Context,
    private val changeEmojiCallback: ChangeEmojiCallback
) : RecyclerView.Adapter<SelectEmojiAdapter.SelectEmojiViewHolder>(){
    private var emojiIdx = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectEmojiViewHolder {
        val binding = ItemSelectEmojiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectEmojiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectEmojiViewHolder, position: Int) {
        holder.bind(emojiList[position], position)
    }

    override fun getItemCount() = emojiList.size

    inner class SelectEmojiViewHolder(
        private val binding: ItemSelectEmojiBinding
    ): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                emojiIdx = adapterPosition
                changeEmojiCallback.onChangeSelectEmojiIndex(emojiIdx)
                notifyDataSetChanged()
            }
        }

        fun bind(emojiResId: Int, position: Int) {
            binding.ivEmojiItem.setImageDrawable(ContextCompat.getDrawable(context, emojiResId))
            if (emojiIdx == position) {
                binding.cbEmojiItem.isChecked = true
                binding.cbEmojiItem.visibility = View.VISIBLE
            } else {
                binding.cbEmojiItem.isChecked = false
                binding.cbEmojiItem.visibility = View.GONE
            }
        }
    }
}