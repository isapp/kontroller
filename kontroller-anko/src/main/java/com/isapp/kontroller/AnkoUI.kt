package com.isapp.kontroller

import android.content.Context
import android.view.View
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.UI

/**
 * A [UI] with a convenience function for creating an [AnkoContext].
 */
interface AnkoUI<in T> : UI<T> {
  /**
   * A convenience function for creating an [AnkoContext].
   *
   * ```
   * class SampleAnkoUI(override val context = context) : AnkoUI<Sample> {
   *   override fun createView(controller: Sample) = usingAnko {
   *     frameLayout()
   *   }
   * }
   * ```
   */
  fun usingAnko(ui: AnkoContext<Context>.() -> View) = AnkoContext.create(context, context).ui()
}
