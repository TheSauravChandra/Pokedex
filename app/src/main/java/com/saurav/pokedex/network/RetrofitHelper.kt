package com.saurav.pokedex.network

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
  val BASE_URL = "https://api.pokemontcg.io/"
  
  fun getInstance(context: Context):Retrofit{
    return Retrofit.Builder()
      .baseUrl(BASE_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }
}