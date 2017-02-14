package com.isapp.kontroller

import android.content.Context
import android.view.View
import org.jetbrains.anko.AnkoContext

interface AnkoUI<in T> : UI<T> {
  val context: Context

  fun usingAnko(ui: AnkoContext<Context>.() -> View) = AnkoContext.create(context, context).ui()
}
