package com.saurav.pokedex.koinDI

import com.saurav.pokedex.adapter.PokeAdapter
import com.saurav.pokedex.network.RetrofitService
import com.saurav.pokedex.vm.PokeRepo
import com.saurav.pokedex.vm.PokeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
  single { RetrofitService.getInstance() }
  factory { PokeRepo(get()) }
  factory { PokeAdapter(get()) }
}

val viewModelModule = module {
  viewModel { PokeViewModel(get()) }
}