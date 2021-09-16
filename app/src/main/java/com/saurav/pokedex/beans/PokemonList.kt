package com.saurav.pokedex.beans

data class PokemonList(
  val count: Int,
  val `data`: List<Pokemon>,
  val page: Int,
  val pageSize: Int,
  val totalCount: Int
)