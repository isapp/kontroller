package com.isapp.kontroller

import android.content.Context
import android.view.View
import android.view.ViewGroup

/**
 * An abstraction around a user interface.
 *
 * All UI related logic should reside in implementations.
 *
 * @param T the type of object that will be passed to [createView]; it is the `UI's` interface with the [UIController].
 */
interface UI<in T> {
  /**
   * The [Context] that the UI lives in.
   */
  val context: Context

  /**
   * Creates a [View] to be handled by the host of the [UIController].
   *
   * @param controller an interface to the `Controller`
   * @param parent an optional parent of the [UI]
   */
  fun createView(controller: T, parent: ViewGroup? = null): View?

  /**
   * Provides an opportunity to clean up views, remove listeners, etc...
   */
  fun destroyView() {}
}
