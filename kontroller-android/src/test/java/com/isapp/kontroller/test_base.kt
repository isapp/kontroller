package com.isapp.kontroller

import android.app.Activity
import com.nhaarman.mockito_kotlin.mock

interface TestController : UIController<TestUI>
interface TestUI : UI<TestController>

val testManagedController: Controller = mock()
val testInternalController: Controller = mock()

class TestManagingController : ManagingController {
  override val managedControllers = listOf(testManagedController)

  override fun initialize() {
    super.initialize()

    testInternalController.initialize()
  }

  override fun start() {
    super.start()

    testInternalController.start()
  }

  override fun stop() {
    super.stop()

    testInternalController.stop()
  }

  override fun destroy() {
    super.destroy()

    testInternalController.destroy()
  }
}

interface TestAndroidUI : UI<TestManagingAndroidUIController>
class TestManagingAndroidUIController(activity: Activity, createUI: () -> TestAndroidUI) : ManagingAndroidUIController<TestAndroidUI>(activity, createUI) {
  override val managedControllers = listOf(testManagedController)

  override fun initialize() {
    super.initialize()

    testInternalController.initialize()
  }

  override fun start() {
    super.start()

    testInternalController.start()
  }

  override fun stop() {
    super.stop()

    testInternalController.stop()
  }

  override fun destroy() {
    super.destroy()

    testInternalController.destroy()
  }
}
