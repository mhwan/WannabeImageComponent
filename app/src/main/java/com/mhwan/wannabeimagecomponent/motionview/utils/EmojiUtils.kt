package com.mhwan.wannabeimagecomponent.motionview.utils

import com.mhwan.wannabeimagecomponent.R

object EmojiUtils {
    private val emojiIds = intArrayOf(
        R.drawable.abra,
        R.drawable.bellsprout,
        R.drawable.bracelet,
        R.drawable.bullbasaur,
        R.drawable.camera,
        R.drawable.candy,
        R.drawable.caterpie,
        R.drawable.charmander,
        R.drawable.mankey,
        R.drawable.map,
        R.drawable.mega_ball,
        R.drawable.meowth,
        R.drawable.pawprints,
        R.drawable.pidgey,
        R.drawable.pikachu,
        R.drawable.pikachu_1,
        R.drawable.pikachu_2,
        R.drawable.player,
        R.drawable.pointer,
        R.drawable.pokebag,
        R.drawable.pokeball,
        R.drawable.pokeballs,
        R.drawable.pokecoin,
        R.drawable.pokedex,
        R.drawable.potion,
        R.drawable.psyduck,
        R.drawable.rattata,
        R.drawable.revive,
        R.drawable.squirtle,
        R.drawable.star,
        R.drawable.star_1,
        R.drawable.superball,
        R.drawable.tornado,
        R.drawable.venonat,
        R.drawable.weedle,
        R.drawable.zubat
    )

    fun getEmojiList() = emojiIds.toList()
}