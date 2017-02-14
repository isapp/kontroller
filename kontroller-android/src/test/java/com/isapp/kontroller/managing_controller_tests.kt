package com.isapp.kontroller

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.robolectric.RobolectricTestRunner

@RunWith(JUnit4::class)
class ManagingControllerTest {
  private val subject = TestManagingController()

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
  private val subject = TestManagingContextController(mock(), { mock() })

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
