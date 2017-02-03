package com.isapp.kontroller

import android.view.View

interface UI<in T : UIController> {
  fun createView(controller: T): View?
}
