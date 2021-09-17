package com.saurav.pokedex.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.saurav.pokedex.R
import com.saurav.pokedex.adapter.PokeAdapter
import com.saurav.pokedex.network.RetrofitService
import com.saurav.pokedex.utils.Constants
import com.saurav.pokedex.utils.Utils.Companion.toast
import com.saurav.pokedex.vm.PokeRepo
import com.saurav.pokedex.vm.PokeViewModel
import com.saurav.pokedex.vm.PokeViewModelFactory

class MainActivity : AppCompatActivity() {
  private lateinit var viewModel: PokeViewModel
  private val retrofitService = RetrofitService.getInstance()
  private val pokeRepo = PokeRepo(retrofitService)
  private val adapter = PokeAdapter(this)
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    viewModel = ViewModelProvider(this, PokeViewModelFactory(pokeRepo)).get(PokeViewModel::class.java)
    handleCardClicks()
    setObservers()
    fetchPokemons()
  }
  
  private fun setObservers() {
    viewModel.pokeList.observe(this, {
      adapter.addList(it)
    })
    
    viewModel.errorMessage.observe(this, {
      toast(it)
    })
    
    viewModel.loading.observe(this, {
    
    })
  }
  
  private fun fetchPokemons() {
    viewModel.getAllPokemons()
  }
  
  private fun handleCardClicks() {
    adapter.attachCallback {
      it?.let {
        startActivity(Intent(this, PokeDetail::class.java).apply {
          putExtra(Constants.POKEMON, it)
        })
      } ?: kotlin.run {
        toast(R.string.some_error_occurred)
      }
    }
  }
  
  
}