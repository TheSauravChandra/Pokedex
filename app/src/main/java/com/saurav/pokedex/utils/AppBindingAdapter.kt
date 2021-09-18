package com.saurav.pokedex.utils

import android.text.TextUtils
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide


object AppBindingAdapter {
  
  /* Loads static images from drawable folder */
  @JvmStatic
  @BindingAdapter("app:loadImage")
  fun loadImage(view: ImageView, @DrawableRes drawable: Int) {
    Glide.with(view.context)
      .load(drawable)
      .into(view)
  }
  
  /* Loads images from server url */
  @BindingAdapter("app:loadImageUrl")
  fun loadImageUrl(view: ImageView, url: String?) {
    Glide.with(view.context)
      .load(url)
      .into(view)
  }
  
}