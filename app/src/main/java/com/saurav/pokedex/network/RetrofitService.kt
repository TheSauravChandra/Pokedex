package com.saurav.pokedex.network

import com.saurav.pokedex.beans.PokemonList
import com.saurav.pokedex.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
  
  @GET(Constants.FETCH_POKEMONS)
  suspend fun getEmAll(@Query("page") page: Int, @Query("pageSize") pageSize: Int): Response<PokemonList>
  // page >=1, pageSize = 5
  
  
  companion object {
    var retrofitService: RetrofitService? = null
    fun getInstance(): RetrofitService {
      if (retrofitService == null)
        retrofitService = RetrofitHelper.getInstance().create(RetrofitService::class.java)
      
      return retrofitService!!
    }
  }
}