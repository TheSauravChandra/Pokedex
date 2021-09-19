package com.saurav.pokedex.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.saurav.pokedex.beans.Pokemon
import kotlinx.coroutines.*

class PokeViewModel(private val mainRepository: PokeRepo) : ViewModel() {
  private val PAGE_SIZE = 20
  var pageNo = 1
  
  val errorMessage = MutableLiveData<String>()
  val pokeList = MutableLiveData(ArrayList<Pokemon>())
  var job: Job? = null
  
  val loading = MutableLiveData(false)
  
  val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
    onError("Exception handled: ${throwable.localizedMessage}")
  }
  
  fun getMorePokemon() {
    loading.postValue(true)
    job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
      val response = mainRepository.getAllPokemons(pageNo, PAGE_SIZE)
      withContext(Dispatchers.Main) {
        if (response.isSuccessful) {
          response.body()?.data?.let {
            ArrayList(pokeList.value).apply {
              addAll(it)
              pokeList.postValue(this)
            }
          }
          ++pageNo
        } else {
          onError("Error : ${response.message()} ")
        }
          loading.postValue(false)
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