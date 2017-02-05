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

interface UIController : Controller {
  fun createView(): View?
  fun uiReady()
  fun navigateBack(): Boolean
}

abstract class ContextController<T : UI<*>> protected constructor(
    protected val context: Context,
    private val createUI: () -> T,
    private val navigateBackAction: () -> Boolean = { false }
) : UIController {
  constructor(fragment: Fragment, createUI: () -> T) : this(fragment.context, createUI, {
    fragment.fragmentManager.popBackStack()
    true
  })
  constructor(activity: Activity, createUI: () -> T) : this(activity as Context, createUI)

  protected var ui: T? = null
  override fun uiReady() {}
  override fun navigateBack() = navigateBackAction()

  @CallSuper
  override fun initialize() {
    ui = createUI()
  }

  override fun createView(): View? {
    return coerceCreateView<UIController, UI<UIController>>()
  }

  @CallSuper
  override fun destroy() {
    ui = null
  }

  private inline fun <reified THIS : UIController, reified UIType : UI<THIS>> coerceCreateView() = (ui as? UIType)?.createView(this as THIS)
}

abstract class ManagingContextController<T : UI<*>> private constructor(
    context: Context,
    createUI: () -> T,
    navigateBackAction: () -> Boolean = { false }
) : ContextController<T>(context, createUI, navigateBackAction), ManagingController  {
  constructor(fragment: Fragment, createUI: () -> T) : this(fragment.context, createUI, {
    fragment.fragmentManager.popBackStack()
    true
  })
  constructor(activity: Activity, createUI: () -> T) : this(activity as Context, createUI)

  @CallSuper
  override fun initialize() {
    super<ManagingController>.initialize()
    super<ContextController>.initialize()
  }

  @CallSuper
  override fun start() {
    super<ManagingController>.start()
    super<ContextController>.start()
  }

  @CallSuper
  override fun stop() {
    super<ManagingController>.stop()
    super<ContextController>.stop()
  }

  @CallSuper
  override fun destroy() {
    super<ManagingController>.destroy()
    super<ContextController>.destroy()
  }
}

class ControllerActivityCallbacks<out T : UIController>(
    private val controller: T
) {
  fun onCreate(activity: Activity) {
    controller.initialize()
    controller.createView()?.apply {
      controller.uiReady()
      activity.setContentView(this)
    }
  }

  fun onResume() = controller.start()
  fun onBackPressed() = controller.navigateBack()
  fun onPause() = controller.stop()
  fun onDestroy() = controller.destroy()
}

class ControllerFragmentCallbacks<out T : UIController>(
    private val controller: T
) {
  fun onCreateView(): View? {
    controller.initialize()
    val view = controller.createView()?.apply {
      controller.uiReady()
    }
    return view
  }

  fun onResume() = controller.start()
  fun onBackPressed() = controller.navigateBack()
  fun onPause() = controller.stop()
  fun onDestroyView() = controller.destroy()
}