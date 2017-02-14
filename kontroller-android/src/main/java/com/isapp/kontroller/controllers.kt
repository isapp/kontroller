package com.isapp.kontroller

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.support.annotation.CallSuper
import android.view.View

interface Controller {
  fun initialize() {}
  fun start() {}
  fun stop() {}
  fun destroy() {}
}

interface ManagingController : Controller {
  val managedControllers: List<Controller>

  @CallSuper
  override fun initialize() {
    managedControllers.forEach { it.initialize() }
  }

  @CallSuper
  override fun start() {
    managedControllers.forEach { it.start() }
  }

  @CallSuper
  override fun stop() {
    managedControllers.forEach { it.stop() }
  }

  @CallSuper
  override fun destroy() {
    managedControllers.forEach { it.destroy() }
  }
}

interface UIController<out T : UI<*>> : Controller {
  val ui: T?

  fun uiReady()
  fun navigateBack(): Boolean
}

abstract class AndroidUIController<T : UI<*>> protected constructor(
    protected val context: Context,
    private val createUI: () -> T,
    private val navigateBackAction: () -> Boolean = { false }
) : UIController<T> {
  constructor(fragment: Fragment, createUI: () -> T) : this(fragment.context, createUI, {
    fragment.fragmentManager.popBackStack()
    true
  })
  constructor(activity: Activity, createUI: () -> T) : this(activity as Context, createUI)

  override var ui: T? = null
  override fun uiReady() {}
  override fun navigateBack() = navigateBackAction()

  @CallSuper
  override fun initialize() {
    ui = createUI()
  }

  @CallSuper
  override fun destroy() {
    ui = null
  }
}

abstract class ManagingAndroidUIController<T : UI<*>> private constructor(
    context: Context,
    createUI: () -> T,
    navigateBackAction: () -> Boolean = { false }
) : AndroidUIController<T>(context, createUI, navigateBackAction), ManagingController  {
  constructor(fragment: Fragment, createUI: () -> T) : this(fragment.activity, createUI, {
    fragment.fragmentManager.popBackStack()
    true
  })
  constructor(activity: Activity, createUI: () -> T) : this(activity as Context, createUI)

  @CallSuper
  override fun initialize() {
    super<ManagingController>.initialize()
    super<AndroidUIController>.initialize()
  }

  @CallSuper
  override fun start() {
    super<ManagingController>.start()
    super<AndroidUIController>.start()
  }

  @CallSuper
  override fun stop() {
    super<ManagingController>.stop()
    super<AndroidUIController>.stop()
  }

  @CallSuper
  override fun destroy() {
    super<ManagingController>.destroy()
    super<AndroidUIController>.destroy()
  }
}

class ControllerActivityCallbacks<in T : R, out R : UIController<U>, out U : UI<T>>(
    private val controller: T
) {
  fun onCreate(activity: Activity) {
    controller.initialize()
    controller.ui?.createView(controller)?.apply {
      controller.uiReady()
      activity.setContentView(this)
    }
  }

  fun onResume() = controller.start()
  fun onBackPressed() = controller.navigateBack()
  fun onPause() = controller.stop()
  fun onDestroy() = controller.destroy()
}

class ControllerFragmentCallbacks<in T : R, R : UIController<U>, out U : UI<R>>(
    private val controller: T
) {
  fun onCreateView(): View? {
    controller.initialize()
    val view = controller.ui?.createView(controller)?.apply {
      controller.uiReady()
    }
    return view
  }

  fun onResume() = controller.start()
  fun onBackPressed() = controller.navigateBack()
  fun onPause() = controller.stop()
  fun onDestroyView() = controller.destroy()
}