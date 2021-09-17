package com.saurav.pokedex.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.saurav.pokedex.R
import com.saurav.pokedex.beans.Pokemon
import com.saurav.pokedex.utils.Constants.POKEMON

class PokeDetail : AppCompatActivity() {
  private var pokemon: Pokemon? = null
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_poke_detail)
    getExtras()
    
    
  }
  
  fun getExtras() {
    intent.extras?.getString(POKEMON)?.let {
      pokemon = Gson().fromJson(it, Pokemon::class.java)
    } ?: run {
      finish()
    }
  }
  
}