package com.saurav.pokedex.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.saurav.pokedex.R
import com.saurav.pokedex.adapter.PokeAdapter
import com.saurav.pokedex.databinding.ActivityMainBinding
import com.saurav.pokedex.network.RetrofitService
import com.saurav.pokedex.utils.Constants
import com.saurav.pokedex.utils.Utils.Companion.toast
import com.saurav.pokedex.vm.PokeRepo
import com.saurav.pokedex.vm.PokeViewModel
import com.saurav.pokedex.vm.PokeViewModelFactory

class MainActivity : AppCompatActivity() {
  private lateinit var viewModel: PokeViewModel
  private val retrofitService = RetrofitService.getInstance()
  private val pokeRepo = PokeRepo(retrofitService)
  private val adapter = PokeAdapter(this)
  private lateinit var binding: ActivityMainBinding
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    setContentView(binding?.root)
    viewModel = ViewModelProvider(this, PokeViewModelFactory(pokeRepo)).get(PokeViewModel::class.java)
    initPage()
    setRecyclerView()
    handleCardClicks()
    setObservers()
    fetchPokemons()
    handleSort()
    handleSearch()
  }
  
  private fun initPage() {
    binding.search.requestFocus()
    binding.btnHpFilter.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_baseline_filter_list_24), null)
    binding.btnLvlFilter.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_baseline_filter_list_24), null)
  }
  
  private fun setRecyclerView() {
    binding.rvList.adapter = adapter
    binding.rvList.layoutManager = LinearLayoutManager(this)
    
    binding.rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy > 0) {
          Log.e("saurav", "dy>0")
//          viewModel.loadMore()
        }
      }
    })
  }
  
  private fun handleSort() {
  
  }
  
  private fun handleSearch() {
  
  }
  
  private fun setObservers() {
    viewModel.pokeList.observe(this, {
      adapter.updateList(it)
    })
    
    viewModel.errorMessage.observe(this, {
      toast(it)
    })
    
    viewModel.loading.observe(this, {
    
    })
  }
  
  private fun fetchPokemons() {
    viewModel.getAllPokemons()
  }
  
  private fun handleCardClicks() {
    adapter.attachCallback {
      it?.let {
        startActivity(Intent(this, PokeDetail::class.java).apply {
          putExtra(Constants.POKEMON, Gson().toJson(it))
        })
      } ?: kotlin.run {
        toast(R.string.some_error_occurred)
      }
    }
  }
  
  
}