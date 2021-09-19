package com.saurav.pokedex.application

import android.app.Application
import com.saurav.pokedex.koinDI.appModule
import com.saurav.pokedex.koinDI.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {
  override fun onCreate() {
    super.onCreate()
    startKoin {
      // declare used Android context
      androidContext(this@MyApp)
      // declare modules
      modules(listOf(appModule, viewModelModule))
    }
  }
}