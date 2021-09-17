package com.saurav.pokedex.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saurav.pokedex.R
import com.saurav.pokedex.beans.Pokemon
import com.saurav.pokedex.databinding.PokeMiniCardBinding

class PokeAdapter(private val context: Context) : RecyclerView.Adapter<PokeAdapter.ViewHolder>() {
  private val list = ArrayList<Pokemon>()
  private var callBack: ((item: Pokemon?) -> Unit)? = null
  
  fun attachCallback(gc: (item: Pokemon?) -> Unit) {
    this.callBack = gc
  }
  
  fun addList(items: ArrayList<Pokemon>) {
    list.addAll(items)
    notifyDataSetChanged()
  }
  
  inner class ViewHolder(var binding: PokeMiniCardBinding) : RecyclerView.ViewHolder(binding.root) {
    
    fun setData(data: Pokemon?) {
      data?.run {
        binding.name = name ?: ""
        images?.small?.let {
          Glide.with(context).load(it).into(binding.ivPic)
        } ?: run {
          binding.ivPic.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_android_black_24dp))
        }
        binding.level = level
        binding.hp = hp
        binding.types = types.toString()
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