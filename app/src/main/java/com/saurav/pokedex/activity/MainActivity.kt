package com.saurav.pokedex.activity

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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
import com.saurav.pokedex.utils.Utils.Companion.checkInternet
import com.saurav.pokedex.utils.Utils.Companion.hideKeyboard
import com.saurav.pokedex.utils.Utils.Companion.toast
import com.saurav.pokedex.vm.PokeRepo
import com.saurav.pokedex.vm.PokeViewModel
import com.saurav.pokedex.vm.PokeViewModelFactory


class MainActivity : AppCompatActivity() {
  private val TAG = "bharat"
  private lateinit var viewModel: PokeViewModel
  private val retrofitService = RetrofitService.getInstance()
  private val pokeRepo = PokeRepo(retrofitService)
  private val adapter = PokeAdapter(this)
  private lateinit var binding: ActivityMainBinding
  private var enabledSearch = false
  private var enabledHPsort = false
  private var enabledLVsort = false
  private var lastMsg = ""
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    setContentView(binding?.root)
    viewModel = ViewModelProvider(this, PokeViewModelFactory(pokeRepo)).get(PokeViewModel::class.java)
    initPage()
    setRecyclerView()
    handleCardClicks()
    setObservers()
    fetchPokemon()
    handleSort()
    handleSearch()
  }
  
  private fun initPage() {
    supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.red)))
    binding.search.onActionViewExpanded() // expanded search
    binding.btnMoveUp.setOnClickListener {
      if ((binding.rvList.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() > 1)
        bringUserToTop()
    }
    hideKeyboard(this, currentFocus)
    clearToggle()
    clearMsg()
  }
  
  private fun setAllUnfilteredToList() {
    adapter.updateList(viewModel.pokeList.value ?: ArrayList())
  }
  
  private fun showUserMsgBar(msg: String) {
    lastMsg = msg
    binding.tvMsg.text = msg
    binding.tvMsg.visibility = View.VISIBLE
  }
  
  private fun clearToggle() {
    binding.btnHpFilter.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_baseline_filter_list_24), null)
    binding.btnLvlFilter.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_baseline_filter_list_24), null)
  }
  
  private fun clearAllQueries() {
    binding.search.setQuery("", true)
  }
  
  private fun bringUserToTop() {
    binding.btnMoveUp.visibility = View.GONE
    binding.rvList.smoothScrollToPosition(0)
  }
  
  private fun clearMsg() {
    binding.tvMsg.visibility = View.GONE
  }
  
  private fun manageSearch(query: String) {
    viewModel.pokeList.value?.let {
      val filtered = it.filter {
        val res = it.name?.contains(query, ignoreCase = true) ?: false
        Log.e(TAG, "name: ${it.name}, id: ${it.id}, CONTAINS res: $res")
        res
      } as ArrayList
      adapter.updateList(filtered)
      showUserMsgBar("Found ${filtered.size} in current fetched ${it.size} Pokemon")
    } ?: run {
      showUserMsgBar("Unable to Search in 0 Pokemon")
    }
  }
  
  private fun shouldLoadMorePokemon(): Boolean {
    val lastVisiblePos = (binding.rvList.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
    return (!(viewModel.loading.value)!! && adapter.itemCount - lastVisiblePos < 2) //PageSize & debounce/singleCall
  }
  
  private fun setRecyclerView() {
    binding.rvList.adapter = adapter
    binding.rvList.layoutManager = LinearLayoutManager(this)
    
    binding.rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
  
        val firstVisiblePos = (binding.rvList.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        val lastVisiblePos = (binding.rvList.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
  
        binding.btnMoveUp.visibility = if (firstVisiblePos != 0) View.VISIBLE else View.GONE
  
        if (enabledSearch) {
          if (adapter.itemCount == lastVisiblePos + 1) {
            binding.tvMsg.text = "Turn Search & Sort OFF, to load more Pokemon"
            binding.tvMsg.setOnClickListener {
              clearAllQueries()
              bringUserToTop()
              clearMsg()
              binding.tvMsg.setOnClickListener {}
            }
          } else {
            binding.tvMsg.text = lastMsg
            binding.tvMsg.setOnClickListener {}
          }
          
          return
        }
        
        if (dy > 0) { //scrolled down. - USER INITIATED
          if (shouldLoadMorePokemon()) {
            fetchPokemon()
          }
        }
      }
    })
  }
  
  private fun handleSort() {
  
  }
  
  private fun handleSearch() {
    binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(query: String?): Boolean {
        enabledSearch = !TextUtils.isEmpty(query)
        return false
      }
  
      override fun onQueryTextChange(newText: String?): Boolean {
        enabledSearch = !TextUtils.isEmpty(newText)
    
        // always in text change
        if (!enabledSearch) {
          setAllUnfilteredToList()
        }
    
        // can be in on query
        if (enabledSearch) {
          showUserMsgBar("Searching \"${newText!!}\" in fetched ${viewModel.pokeList.value?.size ?: 0} Pokemon")
          manageSearch(newText!!)
        }
    
        return false
      }
    });
  }
  
  private fun setObservers() {
    viewModel.pokeList.observe(this, {
      adapter.updateList(it)
      if (viewModel.pageNo > 2)
        binding.rvList.smoothScrollBy(0, 250)
      binding.search.queryHint = "Search in ${it.size}"
    })
  
    viewModel.errorMessage.observe(this, {
      toast(it)
    })
  
    viewModel.loading.observe(this, {
      binding.pbLoading.visibility = if (it) View.VISIBLE else View.GONE
    })
  }
  
  private fun fetchPokemon() {
    if (checkInternet())
      viewModel.getMorePokemon()
    else
      askTurnOnInternet()
  }
  
  private fun askTurnOnInternet() {
  
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