package com.saurav.pokedex.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saurav.pokedex.beans.Pokemon
import com.saurav.pokedex.databinding.PokeMiniCardBinding

class PokeAdapter(private val context: Context) : RecyclerView.Adapter<PokeAdapter.ViewHolder>() {
  private val list = ArrayList<Pokemon>()
  private var callBack: ((item: Pokemon?) -> Unit)? = null
  
  fun attachCallback(gc: (item: Pokemon?) -> Unit) {
    this.callBack = gc
  }
  
  inner class ViewHolder(var binding: PokeMiniCardBinding) : RecyclerView.ViewHolder(binding.root) {
    
    fun setData(data: Pokemon?) {
      data?.run {
        binding.name = name
        
        images?.small?.let {
          binding.photo = it
        }
      }
      
      binding.root.setOnClickListener {
        callBack?.let { it(data) }
      }
      
    }
  }
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = PokeMiniCardBinding.inflate(
      LayoutInflater.from(parent.context),
      parent, false
    )
    return ViewHolder(binding)
  }
  
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.setData(list[position])
    holder.binding.executePendingBindings()
  }
  
  override fun getItemCount() = list.size
  
}