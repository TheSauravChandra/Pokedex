package com.saurav.pokedex.beans

data class Resistance(
  val type: String,
  val value: String
) {
  override fun toString(): String {
    return "$type: $value"
  }
}