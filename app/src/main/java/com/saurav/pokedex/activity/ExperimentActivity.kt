package com.saurav.pokedex.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.lifecycleScope
import com.saurav.pokedex.R
import kotlinx.android.synthetic.main.activity_experiment.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin


data class Pos(var R: Int, var Q: Double, var pace: Float = 0.1f)

class ExperimentActivity : AppCompatActivity() {
  var t: Double = 0.0;
  var n = 7
  val A = 150
  val dR = 100
  var centerX = 500
  var centerY = 700
  var touchX = 500
  var touchY = 700
  
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_experiment)
    initGame()
    
    body.setOnTouchListener { _, motionEvent ->
      touchX = motionEvent.x.toInt()
      touchY = motionEvent.y.toInt()

//      when (motionEvent.action) {
//        MotionEvent.ACTION_DOWN -> Log.i("TAG", "touched down")
//        MotionEvent.ACTION_MOVE -> {
//          Log.i("TAG", "moving: (" + x + ", " + y + ")")
//        }
//        MotionEvent.ACTION_UP -> Log.i("TAG", "touched up")
//        else -> {}
//      }
      
      return@setOnTouchListener true
    }
    
  }
  
  private fun initGame() {
    
    for (i in 1..n) {
      body.addView(
        View(this).apply {
          id = i
          val temp = Math.random()
          val R = A + dR * Math.random()
          x = (centerX + sin(temp) * R).toFloat()
          y = (centerY + cos(temp) * R).toFloat()
          setBackgroundColor(
            when (i % 3) {
              1 -> Color.RED
              2 -> Color.BLUE
              3 -> Color.GREEN
              0 -> Color.YELLOW
              else -> Color.CYAN
            }
          )
          tag = Pos(R.toInt(), temp, temp.toFloat())
          layoutParams = ViewGroup.LayoutParams(20, 20)
        }
      )
    }
    
    lifecycleScope.launch {
      withContext(Main) {
        while (true) {
          body.children.forEach {
            var pos = (it.tag as Pos)
            pos.Q += pos.pace

//            if(it.id==1)
//              Log.e("bharat","A:${A}, centre:$centerX $centerY, x:${it.x} added ${A * cos(pos.temp)}, y:${it.y} added ${-1 * A * sin(pos.temp)}, tag:${it.tag}")
            
            it.x = (centerX + sin(pos.Q) * pos.R).toFloat()
            it.y = (centerY + cos(pos.Q) * pos.R).toFloat()
            
            if (touchX != centerX)
              centerX += sign((touchX - centerX).toDouble()).toInt()
            
            if (touchY != centerY)
              centerY += sign((touchY - centerY).toDouble()).toInt()
            
            it.tag = pos
          }
          delay(10)
          
        }
      }
    }
    
    
  }
  
  
}