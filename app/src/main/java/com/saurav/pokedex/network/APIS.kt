package com.saurav.pokedex.network

import com.saurav.pokedex.beans.PokemonList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIS {
  
  @GET("v2/cards")
  suspend fun getEmAll(@Query("page") page:Int, @Query("pageSize") pageSize:Int) : Response<PokemonList>
  // page >=1, pageSize = 5
  
}