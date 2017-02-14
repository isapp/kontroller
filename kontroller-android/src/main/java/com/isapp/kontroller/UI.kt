package com.isapp.kontroller

import android.view.View

interface UI<in T> {
  fun createView(controller: T): View?
}
