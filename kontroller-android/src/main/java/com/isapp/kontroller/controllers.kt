package com.isapp.kontroller

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.annotation.CallSuper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.IdentityHashMap
import android.support.v4.app.Fragment as SupportFragment

private val managedControllersLookup = IdentityHashMap<Controller, ManagingController>()

/**
 * A `Controller` provides a simple lifecycle to build an abstraction around complex logic.
 *
 * By keeping the lifecycle simple, a controller can focus on a specific scope of functionality; anything else can be
 * delegated to another controller.
 *
 * The lifecycle is:
 *
 * 1. [initialize]
 * 2. [start]
 * 3. [stop]
 * 4. `goto #2` | [destroy]
 *
 * Note that a single controller instance may go through multiple lifecycles. Therefore, functions that are only called
 * once per lifecycle ([initialize], [destroy], etc...) may be called multiple times per instance, but always in the
 * order of the lifecycle.
 *
 * @see [ControllerActivityCallbacks], [ControllerFragmentCallbacks] for an example of the controller lifecycle is managed
 */
interface Controller {
  /**
   * The beginning of the lifecycle.
   *
   * Should be used to allocate any resources that will live through the controller's lifecycle.
   */
  fun initialize() {}

  /**
   * Starts the controller's work.
   *
   * Should be used to allocate any resources the controller needs while doing its work.
   */
  fun start() {}

  /**
   * Stops the controller's work.
   *
   * Should be used to release any resources that were allocated in [start].
   */
  fun stop() {}

  /**
   * The end of the lifecycle.
   *
   * Should be used to release any resources that were allocated in [initialize].
   */
  fun destroy() {}
}

/**
 * A [Controller] that manages the lifecycles of other `controllers`.
 *
 * Implemented functions *must* call through to their superclass's functions.
 */
interface ManagingController : Controller {
  /**
   * A [List<Controller>] of [Controller] that this [ManagingController] will manage.
   *
   * On every lifecycle call, the corresponding lifecycle call will be invoked on each `Controller` in this list.
   */
  val managedControllers: List<Controller>

  @CallSuper
  override fun initialize() {
    managedControllers.forEach {
      // throw if a different ManagingController is already managing this Controller
      val managingController = managedControllersLookup.getOrPut(it, { this })
      if(managingController != this) {
        throw IllegalStateException("$it is already managed by $managingController")
      }

      it.initialize()
    }
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
    managedControllers.forEach {
      managedControllersLookup.remove(it)
      it.destroy()
    }
  }
}

/**
 * An extension of [Controller] that adds lifecycle methods for dealing with [UI].
 *
 * The lifecycle is:
 *
 * 1. [initialize]
 * 2. [uiReady]
 * 3. [start]
 * 4. [stop]
 * 5. `goto #3` | [destroy]
 *
 * @param T The type of [UI] that this controller will be attached to.
 */
interface UIController<out T : UI<*>> : Controller {
  /**
   * A [UI] that will be displayed to the user.
   */
  val ui: T?

  /**
   * This function should not be called until [ui] is ready to be used.
   *
   * Ideally, it will be called before the [UI] is displayed to the user.
  */
  fun uiReady()

  /**
   * This function will called when the user navigates back.
   *
   * @return whether or not it handled the back navigation.
   */
  fun navigateBack(): Boolean
}

/**
 * `AndroidUIController` is an implementation of [UIController] that is driven by an [Activity] or [Fragment] (native or support).
 *
 * # [UI]
 *
 * `AndroidUIController manages the lifecycle of its [ui] object. When [initialize] is called, it will create a [UI]
 * using the [createUI] factory passed in the constructor. When [destroy] is called, it will set [ui] to `null`.
 *
 * # [navigateBack]
 * `AndroidUIController` will exhibit different behavior for `navigateBack` depending on which constructor is used.
 *
 * If a [Fragment] or [android.support.v4.app.Fragment] is passed, `navigateBack` will call [android.app.FragmentManager.popBackStack].
 *
 * If an [Activity] is passed, `navigateBack` will return `false`.
 *
 * @param T The type of [UI] that this controller will be attached to.
 */
abstract class AndroidUIController<T : UI<*>> protected constructor(
    protected val context: Context,
    private val createUI: () -> T,
    private val navigateBackAction: () -> Boolean = { false }
) : UIController<T> {
  constructor(fragment: Fragment, createUI: () -> T) : this(fragment.activity, createUI, {
    fragment.fragmentManager.popBackStack()
    true
  })
  constructor(fragment: SupportFragment, createUI: () -> T) : this(fragment.activity, createUI, {
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

/**
 * A convenience class that extends [AndroidUIController] and implements [ManagingController].
 */
abstract class ManagingAndroidUIController<T : UI<*>> private constructor(
    context: Context,
    createUI: () -> T,
    navigateBackAction: () -> Boolean = { false }
) : AndroidUIController<T>(context, createUI, navigateBackAction), ManagingController  {
  constructor(fragment: Fragment, createUI: () -> T) : this(fragment.activity, createUI, {
    fragment.fragmentManager.popBackStack()
    true
  })
  constructor(fragment: SupportFragment, createUI: () -> T) : this(fragment.activity, createUI, {
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

/**
 * `ControllerActivityCallbacks` provides functions corresponding to the [Activity] lifecycle. All an [Activity] has to
 * do to manage a [UIController] is call through to these corresponding functions.
 *
 * @see [ControllerActivity]
 *
 * @param T the type of object the [UI] will receive in [UI.createView]. It must be a [UIController], but it can be a super-interface so that multiple `UIControllers` can use this [UI]
 * @param R the type of [UIController]
 * @param U the type of [UI] the [UIController] manages
 */
class ControllerActivityCallbacks<in T : R, out R : UIController<U>, out U : UI<T>>(
    private val controller: T
) {

  /**
   * Should be called from [Activity.onCreate].
   */
  fun onCreate(activity: Activity) {
    controller.initialize()
    controller.ui?.createView(controller)?.apply {
      controller.uiReady()
      activity.setContentView(this)
    }
  }

  /**
   * Should be called from [Activity.onResume].
   */
  fun onResume() = controller.start()

  /**
   * Should be called from [Activity.onBackPressed].
   */
  fun onBackPressed() = controller.navigateBack()

  /**
   * Should be called from [Activity.onPause].
   */
  fun onPause() = controller.stop()

  /**
   * Should be called from [Activity.onDestroy].
   */
  fun onDestroy() = controller.destroy()
}

/**
 * A convenience [Activity] for managing a [UIController].
 */
abstract class ControllerActivity<C : UIController<U>, out U : UI<C>> : Activity() {
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

/**
 * `ControllerFragmentCallbacks` provides functions corresponding to the [Fragment] (or [android.support.v4.app.Fragment]) lifecycle. All a `Fragment` has to
 * do to manage a [UIController] is call through to these corresponding functions.
 *
 * @see [ControllerFragment]
 *
 * @param T the type of object the [UI] will receive in [UI.createView]. It must be a [UIController], but it can be a super-interface so that multiple `UIControllers` can use this [UI]
 * @param R the type of [UIController]
 * @param U the type of [UI] the [UIController] manages
 */
class ControllerFragmentCallbacks<in T : R, R : UIController<U>, out U : UI<R>>(
    private val controller: T
) {

  /**
   * Should be called from [Fragment.onCreateView]
   */
  fun onCreateView(): View? {
    controller.initialize()
    val view = controller.ui?.createView(controller)?.apply {
      controller.uiReady()
    }
    return view
  }

  /**
   * Should be called from [Fragment.onResume]
   */
  fun onResume() = controller.start()

  /**
   * Should be called from [OnBackPressed.onBackPressed]
   */
  fun onBackPressed() = controller.navigateBack()

  /**
   * Should be called from [Fragment.onPause]
   */
  fun onPause() = controller.stop()

  /**
   * Should be called from [Fragment.onDestroyView]
   */
  fun onDestroyView() = controller.destroy()
}

/**
 * An interface for adding `onBackPressed` to `Fragments`.
 */
interface OnBackPressed {
  fun onBackPressed(): Boolean
}

/**
 * A convenience [Fragment] for managing a [UIController].
 */
abstract class ControllerFragment<C : UIController<U>, out U : UI<C>> : Fragment(), OnBackPressed {
  protected abstract val controller: C
  private lateinit var callbacks: ControllerFragmentCallbacks<C, C, U>

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    callbacks = ControllerFragmentCallbacks(controller)
    return callbacks.onCreateView()
  }

  override fun onResume() {
    super.onResume()
    callbacks.onResume()
  }

  override fun onBackPressed() = callbacks.onBackPressed()

  override fun onPause() {
    super.onPause()
    callbacks.onPause()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    callbacks.onDestroyView()
  }
}

/**
 * A convenience [android.support.v4.app.Fragment] for managing a [UIController].
 */
abstract class ControllerSupportFragment<C : UIController<U>, out U : UI<C>> : SupportFragment(), OnBackPressed {
  protected abstract val controller: C
  private lateinit var callbacks: ControllerFragmentCallbacks<C, C, U>

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    callbacks = ControllerFragmentCallbacks(controller)
    return callbacks.onCreateView()
  }

  override fun onResume() {
    super.onResume()
    callbacks.onResume()
  }

  override fun onBackPressed() = callbacks.onBackPressed()

  override fun onPause() {
    super.onPause()
    callbacks.onPause()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    callbacks.onDestroyView()
  }
}