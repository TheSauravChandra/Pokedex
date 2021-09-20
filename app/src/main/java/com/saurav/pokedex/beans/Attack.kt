package com.saurav.pokedex.beans

data class Attack(
  val convertedEnergyCost: Int,
  val cost: List<String>,
  val damage: String,
  val name: String,
  val text: String
) {
  override fun toString(): String {
    return "@\"$name\":$text\n"
  }
}