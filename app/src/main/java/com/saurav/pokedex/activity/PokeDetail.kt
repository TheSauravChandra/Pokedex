package com.saurav.pokedex.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.saurav.pokedex.R
import com.saurav.pokedex.beans.Pokemon
import com.saurav.pokedex.databinding.ActivityPokeDetailBinding
import com.saurav.pokedex.utils.Constants.POKEMON

class PokeDetail : AppCompatActivity() {
  private var pokemon: Pokemon? = null
  private lateinit var binding: ActivityPokeDetailBinding
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    binding = DataBindingUtil.setContentView(this, R.layout.activity_poke_detail)
    setContentView(binding?.root)
    getExtras()
    initiate()
    setData()
  }
  
  fun initiate() {
    supportActionBar?.hide()
  }
  
  fun setData() {
    with(pokemon!!) {
      binding.name = name ?: ""
      
      // cached SD
      images?.small?.let {
        Glide.with(this@PokeDetail)
          .load(it)
          .into(binding.ivPic)
      }
      // HD
      images?.large?.let {
        Glide.with(this@PokeDetail)
          .load(it)
          .into(binding.ivPicHD)
      }
      
      binding.types = types?.toString()
      binding.subTypes = subtypes?.toString()
      binding.level = level
      binding.hp = hp
      binding.attacks = attacks?.toString()
      binding.weakness = weaknesses?.toString()
      binding.abilities = abilities?.toString()
      binding.resistances = resistances?.toString()
    }
    
    findViewById<View>(android.R.id.content).setOnClickListener {
      super.onBackPressed()
    }
  }
  
  fun getExtras() {
    intent.extras?.getString(POKEMON)?.let {
      pokemon = Gson().fromJson(it, Pokemon::class.java)
      if (pokemon == null)
        finish()
    } ?: run {
      finish()
    }
  }
  
}