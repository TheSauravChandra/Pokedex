package com.saurav.pokedex.beans

data class Ability(
  val name: String,
  val text: String,
  val type: String
) {
  override fun toString(): String {
    return "@\"$name\": $text\n**[$type]**\n"
  }
}