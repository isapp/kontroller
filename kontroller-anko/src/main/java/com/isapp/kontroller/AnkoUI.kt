package com.isapp.kontroller

import android.content.Context
import android.view.View
import org.jetbrains.anko.AnkoContext

abstract class AnkoUI<in T : UIController>(private val context: Context) : UI<T> {
  fun withContext(ui: AnkoContext<Context>.() -> View) = AnkoContext.create(context, context).ui()
}
