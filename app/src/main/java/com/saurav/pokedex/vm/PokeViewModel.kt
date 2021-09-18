package com.saurav.pokedex.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.saurav.pokedex.beans.Pokemon
import kotlinx.coroutines.*

class PokeViewModel(private val mainRepository: PokeRepo) : ViewModel() {
  private val PAGE_SIZE = 20
  private var PAGE_NUM = 1
  
  val errorMessage = MutableLiveData<String>()
  val pokeList = MutableLiveData<ArrayList<Pokemon>>()
  var job: Job? = null
  
  val loading = MutableLiveData<Boolean>()
  
  val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
    onError("Exception handled: ${throwable.localizedMessage}")
  }
  
  fun getAllPokemons() {
    loading.postValue(true)
    job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
      val response = mainRepository.getAllPokemons(PAGE_NUM, PAGE_SIZE)
      withContext(Dispatchers.Main) {
        if (response.isSuccessful) {
          pokeList.postValue(response.body()?.data)
          loading.postValue(false)
          ++PAGE_NUM
        } else {
          onError("Error : ${response.message()} ")
        }
      }
    }
    
  }
  
  private fun onError(message: String) {
    errorMessage.postValue(message)
    loading.postValue(false)
  }
  
  override fun onCleared() {
    super.onCleared()
    job?.cancel()
  }
  
}