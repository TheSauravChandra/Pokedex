package com.saurav.pokedex.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saurav.pokedex.R
import com.saurav.pokedex.beans.Pokemon
import com.saurav.pokedex.databinding.PokeMiniCardBinding

class PokeAdapter(private val context: Context) : RecyclerView.Adapter<PokeAdapter.ViewHolder>() {
  private var list = ArrayList<Pokemon>()
  private var callBack: ((item: Pokemon?, card: PokeMiniCardBinding) -> Unit)? = null
  
  fun attachCallback(gc: (item: Pokemon?, card: PokeMiniCardBinding) -> Unit) {
    this.callBack = gc
  }
  
  fun getList() = list
  
  fun updateList(items: ArrayList<Pokemon>) {
    val old = list
    val diffRes: DiffUtil.DiffResult = DiffUtil.calculateDiff(PokeDiffUtil(old, items))
    list = items
    diffRes.dispatchUpdatesTo(this)
  }
  
  inner class ViewHolder(var binding: PokeMiniCardBinding) : RecyclerView.ViewHolder(binding.root) {
    
    fun setData(data: Pokemon?) {
      data?.run {
        val sh = binding.shimmer
        sh.startShimmer()
        binding.name = name ?: ""
        images?.small?.let {
          Glide.with(context)
            .load(it)
            .into(binding.ivPic)
        } ?: run {
          binding.ivPic.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_android_black_24dp))
        }
        binding.level = level
        binding.hp = hp
        binding.types = types.toString()
      }
      
      binding.root.setOnClickListener {
        callBack?.let { it(data, binding) }
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
  
  class PokeDiffUtil(private val oldList: ArrayList<Pokemon>, private val newList: ArrayList<Pokemon>) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    
    override fun getNewListSize() = newList.size
    
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
      return oldList[oldItemPosition].id == newList[newItemPosition].id
    }
    
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
      return oldList[oldItemPosition] == newList[newItemPosition]
    }
    
  }
  
}