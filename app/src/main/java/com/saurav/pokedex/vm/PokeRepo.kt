package com.saurav.pokedex.vm

import com.saurav.pokedex.network.RetrofitService

class PokeRepo constructor(private val retrofitService: RetrofitService) {
  
  suspend fun getAllPokemons(pageNo: Int, pageSize: Int) = retrofitService.getEmAll(pageNo, pageSize)
  
}