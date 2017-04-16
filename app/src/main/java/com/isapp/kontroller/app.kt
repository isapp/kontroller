package com.isapp.kontroller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

interface AppInterface {
  val context: Context
}

class AppUI(
    override val context: Context
) : UI<AppInterface> {
  override fun createView(controller: AppInterface, parent: ViewGroup?): View {
    val view = LayoutInflater.from(controller.context).inflate(R.layout.activity, null)
    view.findViewById(R.id.button).setOnClickListener {
      controller.context.startActivity(Intent(controller.context, OtherActivity::class.java))
    }
    return view
  }
}

class AppController(override val context: Context) : UIController<AppUI>, AppInterface {
  override val ui = AppUI(context)

  override fun uiReady() {}
  override fun navigateBack() = false
}

class OtherController(override val context: Context) : UIController<AppUI>, AppInterface {
  override val ui = AppUI(context)

  override fun uiReady() {}
  override fun navigateBack() = false
}

abstract class BaseActivity<C : UIController<U>, out U : UI<C>> : AppCompatActivity() {
  protected abstract val controller: C
  private lateinit var callbacks: ControllerActivityCallbacks<C, C, U>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    callbacks = ControllerActivityCallbacks(controller)
    callbacks.onCreate(this)
  }

  override fun onResume() {
    super.onResume()
    callbacks.onResume()
  }

  override fun onBackPressed() {
    super.onBackPressed()
    callbacks.onBackPressed()
  }

  override fun onPause() {
    super.onPause()
    callbacks.onPause()
  }

  override fun onDestroy() {
    super.onDestroy()
    callbacks.onDestroy()
  }
}

class FirstActivity : BaseActivity<AppController, AppUI>() {
  override val controller = AppController(this)
}

class OtherActivity : BaseActivity<OtherController, AppUI>() {
  override val controller = OtherController(this)
}
