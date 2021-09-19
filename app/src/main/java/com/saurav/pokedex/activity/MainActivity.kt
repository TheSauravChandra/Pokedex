package com.saurav.pokedex.activity

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Pair
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.saurav.pokedex.R
import com.saurav.pokedex.adapter.PokeAdapter
import com.saurav.pokedex.beans.FilterEnum
import com.saurav.pokedex.databinding.ActivityMainBinding
import com.saurav.pokedex.network.RetrofitService
import com.saurav.pokedex.utils.Constants
import com.saurav.pokedex.utils.Utils.Companion.checkInternet
import com.saurav.pokedex.utils.Utils.Companion.hideKeyboard
import com.saurav.pokedex.utils.Utils.Companion.toast
import com.saurav.pokedex.vm.PokeRepo
import com.saurav.pokedex.vm.PokeViewModel
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
  private val TAG = "bharat"
  private val viewModel: PokeViewModel by viewModel<PokeViewModel>()
  private val adapter: PokeAdapter by inject<PokeAdapter>()
  private lateinit var binding: ActivityMainBinding
  private var enabledSearch = false
  private var filterHPsort: FilterEnum = FilterEnum.OFF
  private var filterLVsort: FilterEnum = FilterEnum.OFF
  private var lastMsg = ""
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    setContentView(binding?.root)
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
    clearAllSort()
    clearMsg()
  }
  
  private fun toggleLifecycle(currentState: FilterEnum): FilterEnum {
    return when (currentState) {
      FilterEnum.OFF -> FilterEnum.DSC
      FilterEnum.DSC -> FilterEnum.ASC
      FilterEnum.ASC -> FilterEnum.OFF
    }
  }
  
  private fun handleSortBtn(state: FilterEnum, btn: Button) {
    btn.setCompoundDrawablesWithIntrinsicBounds(null, null,
      when (state) {
        FilterEnum.OFF -> {
          ContextCompat.getDrawable(this, R.drawable.ic_baseline_filter_list_24)?.apply {
            DrawableCompat.setTint(this, ContextCompat.getColor(this@MainActivity, R.color.grey))
          }
        }
        FilterEnum.ASC -> {
          ContextCompat.getDrawable(this, R.drawable.ic_baseline_filter_asc_list_24)?.apply {
            DrawableCompat.setTint(this, ContextCompat.getColor(this@MainActivity, R.color.black))
          }
        }
        FilterEnum.DSC -> {
          ContextCompat.getDrawable(this, R.drawable.ic_baseline_filter_list_24)?.apply {
            DrawableCompat.setTint(this, ContextCompat.getColor(this@MainActivity, R.color.black))
          }
        }
      }, null)
  }
  
  
  private fun setAllUnfilteredUnSortedToList() {
    adapter.updateList(viewModel.pokeList.value ?: ArrayList())
  }
  
  private fun showUserMsgBar(msg: String) {
    lastMsg = msg
    binding.tvMsg.text = msg
    binding.tvMsg.visibility = View.VISIBLE
  }
  
  private fun clearAllQueries() {
    binding.search.setQuery("", true)
  }
  
  private fun clearAllSort() {
    filterLVsort = FilterEnum.OFF
    handleSortBtn(filterLVsort, binding.btnLvlFilter)
    filterHPsort = FilterEnum.OFF
    handleSortBtn(filterHPsort, binding.btnHpFilter)
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
      val filtered = it.filter { it.name?.contains(query, ignoreCase = true) ?: false } as ArrayList
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
  
        val firstVisiblePos = (binding.rvList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        val lastVisiblePos = (binding.rvList.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
  
        binding.btnMoveUp.visibility = if (firstVisiblePos != 0) View.VISIBLE else View.GONE
  
        if (enabledSearch) {
          if (adapter.itemCount == lastVisiblePos + 1) {
            binding.tvMsg.text = "Clear Search, to load more Pokemon"
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
  
        if (filterHPsort != FilterEnum.OFF || filterLVsort != FilterEnum.OFF) {
    
          if (adapter.itemCount == lastVisiblePos + 1) {
            binding.tvMsg.text = "Turn Sort OFF, to load more Pokemon"
            binding.tvMsg.setOnClickListener {
              clearAllSort()
              bringUserToTop()
              clearMsg()
              setAllUnfilteredUnSortedToList()
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
    binding.btnHpFilter.setOnClickListener {
//          turn off other
      if (filterLVsort != FilterEnum.OFF) {
        filterLVsort = FilterEnum.OFF
        handleSortBtn(FilterEnum.OFF, binding.btnLvlFilter)
      }
      
      filterHPsort = toggleLifecycle(filterHPsort)
      manageSort(filterHPsort, true)
      handleSortBtn(filterHPsort, binding.btnHpFilter)
    }
    
    
    binding.btnLvlFilter.setOnClickListener {
//           turn off other
      if (filterHPsort != FilterEnum.OFF) {
        filterHPsort = FilterEnum.OFF
        handleSortBtn(FilterEnum.OFF, binding.btnHpFilter)
      }
      
      
      filterLVsort = toggleLifecycle(filterLVsort)
      manageSort(filterLVsort, false)
      handleSortBtn(filterLVsort, binding.btnLvlFilter)
    }
  }
  
  private fun manageSort(filter: FilterEnum, HpOrLvl: Boolean) {
    adapter.getList().let {
      var list = it
      
      when (filter) {
        FilterEnum.OFF -> {
          // recalculate search from global source, as previous order = ASC always
          list = viewModel.pokeList.value!!
            .filter { it.name?.contains(binding.search.query, ignoreCase = true) ?: false }
              as ArrayList
          binding.tvMsg.visibility = View.GONE
        }
        FilterEnum.ASC -> {
          if (HpOrLvl)
            list.sortBy { it.hp?.toInt() ?: 0 }
          else
            list.sortBy { it.level?.toInt() ?: 0 }
        }
        FilterEnum.DSC -> {
          if (HpOrLvl)
            list.sortByDescending { it.hp?.toInt() ?: 0 }
          else
            list.sortByDescending { it.level?.toInt() ?: 0 }
        }
      }
  
      adapter.updateList(list)
      adapter.notifyDataSetChanged() // have to understand, why UI doesn't update with Diff Util here.
  
      if (filter != FilterEnum.OFF)
        showUserMsgBar("${if (filter == FilterEnum.DSC) "Descending" else "Ascending"} Sorted by ${if (HpOrLvl) "HP" else "Level"} in ${it.size} Pokemon")
    } ?: run {
      showUserMsgBar("Unable to Sort in 0 Pokemon")
    }
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
          setAllUnfilteredUnSortedToList()
          bringUserToTop()
          clearMsg()
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
    with(AlertDialog.Builder(this))
    {
      setTitle("Please Turn ON Internet")
      setMessage("After that you can retry:")
      setPositiveButton("Retry") { p0, p1 ->
        fetchPokemon()
      }
      setCancelable(false)
      setFinishOnTouchOutside(false)
      setNeutralButton("Leave App") { _, _ -> finish() }
      show()
    }
  }
  
  private fun handleCardClicks() {
    adapter.attachCallback { data, card ->
      data?.let {
      
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        
          val options = ActivityOptions.makeSceneTransitionAnimation(
            this,
            Pair(card.ivPic, "photo"), Pair(card.mcvRoundedBg, "card")
          )
        
          startActivity(Intent(this, PokeDetail::class.java).apply {
            putExtra(Constants.POKEMON, Gson().toJson(it))
          }, options.toBundle())
        
        } else {
          startActivity(Intent(this, PokeDetail::class.java).apply {
            putExtra(Constants.POKEMON, Gson().toJson(it))
          })
        }
      
      } ?: kotlin.run {
        toast(R.string.some_error_occurred)
      }
    }
  }
  
  
}