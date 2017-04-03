package com.isapp.kontroller

import android.view.View

/**
 * An abstraction around a user interface.
 *
 * All UI related logic should reside in implementations.
 *
 * @param T the type of object that will be passed to [createView]; it is the `UI's` interface with the [UIController].
 */
interface UI<in T> {
  /**
   * Creates a [View] to be handled by the host of the [UIController].
   *
   * @param controller an interface to the `Controller`
   */
  fun createView(controller: T): View?

  /**
   * Provides an opportunity to clean up views, remove listeners, etc...
   */
  fun destroyView() {}
}
