package com.saurav.pokedex.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

class Utils {
  
  companion object {
    
    @JvmStatic
    fun Context.toast(msg: String = "") {
      Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
    
    @JvmStatic
    fun Context.toast(@StringRes resId: Int) {
      Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }
  }
}