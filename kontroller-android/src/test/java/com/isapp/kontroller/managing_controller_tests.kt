package com.isapp.kontroller

import android.app.Activity
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.robolectric.RobolectricTestRunner

private val testManagedController: Controller = mock()
private val testInternalController: Controller = mock()

private class TestController : ManagingController {
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

private interface TestUI : UI<TestContextController>
private class TestContextController(activity: Activity, createUI: () -> TestUI) : ManagingContextController<TestUI>(activity, createUI) {
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

@RunWith(JUnit4::class)
class ManagingControllerTest {
  private val subject = TestController()

  @Test
  fun initialize_callsInitializeAndManagedControllerInitialize() {
    subject.initialize()

    verify(testManagedController).initialize()
    verify(testInternalController).initialize()
  }

  @Test
  fun start_callsManagedControllerStart() {
    subject.start()

    verify(testManagedController).start()
    verify(testInternalController).start()
  }

  @Test
  fun stop_callsManagedControllerStop() {
    subject.stop()

    verify(testManagedController).stop()
    verify(testInternalController).stop()
  }

  @Test
  fun destroy_callsManagedControllerDestroy() {
    subject.destroy()

    verify(testManagedController).destroy()
    verify(testInternalController).destroy()
  }

  @After
  fun tearDown() {
    reset(testManagedController)
    reset(testInternalController)
  }
}

@RunWith(RobolectricTestRunner::class)
class ManagingContextControllerTest {
  private val subject = TestContextController(mock(), { mock() })

  @Test
  fun initialize_callsInitializeAndManagedControllerInitialize() {
    subject.initialize()

    verify(testManagedController).initialize()
    verify(testInternalController).initialize()
  }

  @Test
  fun start_callsManagedControllerStart() {
    subject.start()

    verify(testManagedController).start()
    verify(testInternalController).start()
  }

  @Test
  fun stop_callsManagedControllerStop() {
    subject.stop()

    verify(testManagedController).stop()
    verify(testInternalController).stop()
  }

  @Test
  fun destroy_callsManagedControllerDestroy() {
    subject.destroy()

    verify(testManagedController).destroy()
    verify(testInternalController).destroy()
  }

  @After
  fun tearDown() {
    reset(testManagedController)
    reset(testInternalController)
  }
}
