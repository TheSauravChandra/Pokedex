package com.saurav.pokedex.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PokeViewModelFactory constructor(private val repository: PokeRepo) : ViewModelProvider.Factory {
  
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return if (modelClass.isAssignableFrom(PokeViewModel::class.java)) {
      PokeViewModel(this.repository) as T
    } else {
      throw IllegalArgumentException("ViewModel Not Found")
    }
  }
}