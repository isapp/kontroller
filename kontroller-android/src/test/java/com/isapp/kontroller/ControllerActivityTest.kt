package com.isapp.kontroller

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.isNull
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.util.ActivityController

@RunWith(RobolectricTestRunner::class)
class ControllerActivityTest {
  companion object {
    private val mockUI = mock<TestUI>()
    private val mockController = mock<TestController>()
    private val mockView = mock<View>()
  }

  private lateinit var activityController: ActivityController<TestActivityControllerActivity>

  @Before
  fun beforeTest() {
    activityController = Robolectric.buildActivity(TestActivityControllerActivity::class.java)
    whenever(mockController.ui).doReturn(mockUI)
    whenever(mockUI.createView(any(), isNull())).doReturn(mockView)
  }

  @Test
  fun onCreate_callsInitialize_thenCreateView_thenUIReady() {
    activityController.create()

    inOrder(mockController, mockUI) {
      verify(mockController).initialize()
      verify(mockController).ui
      verify(mockUI).createView(mockController, null)
      verify(mockController).uiReady()
    }

    verifyNoMoreInteractions(mockController)
  }

  @Test
  fun onCreate_ifThereIsNoView_callsInitialize_thenCreateView_butNotUIReady() {
    whenever(mockUI.createView(any(), isNull())).doReturn(null as View?)
    activityController.create()

    inOrder(mockController, mockUI) {
      verify(mockController).initialize()
      verify(mockController).ui
      verify(mockUI).createView(mockController, null)
      verify(mockController, never()).uiReady()
    }

    verifyNoMoreInteractions(mockController)
  }

  @Test
  fun onStart_callsNothingAfterUIReady() {
    activityController.create().start()

    inOrder(mockController, mockUI) {
      verify(mockController).initialize()
      verify(mockController).ui
      verify(mockUI).createView(mockController, null)
      verify(mockController).uiReady()
    }

    verifyNoMoreInteractions(mockController)
  }

  @Test
  fun onResume_callsStart() {
    activityController.create().start().resume()

    inOrder(mockController, mockUI) {
      verify(mockController).initialize()
      verify(mockController).ui
      verify(mockUI).createView(mockController, null)
      verify(mockController).uiReady()
      verify(mockController).start()
    }

    verifyNoMoreInteractions(mockController)
  }

  @Test
  fun onBackPressed_callsNavigateBack() {
    activityController.create().start().resume().get().onBackPressed()

    inOrder(mockController, mockUI) {
      verify(mockController).initialize()
      verify(mockController).ui
      verify(mockUI).createView(mockController, null)
      verify(mockController).uiReady()
      verify(mockController).start()
      verify(mockController).navigateBack()
    }

    verifyNoMoreInteractions(mockController)
  }

  @Test
  fun onPause_callsStop() {
    activityController.create().start().resume().pause()

    inOrder(mockController, mockUI) {
      verify(mockController).initialize()
      verify(mockController).ui
      verify(mockUI).createView(mockController, null)
      verify(mockController).uiReady()
      verify(mockController).start()
      verify(mockController).stop()
    }

    verifyNoMoreInteractions(mockController)
  }

  @Test
  fun onStop_callsNothingAfterStop() {
    activityController.create().start().resume().pause().stop()

    inOrder(mockController, mockUI) {
      verify(mockController).initialize()
      verify(mockController).ui
      verify(mockUI).createView(mockController, null)
      verify(mockController).uiReady()
      verify(mockController).start()
      verify(mockController).stop()
    }

    verifyNoMoreInteractions(mockController)
  }

  @Test
  fun onDestroy_callsDestroy() {
    activityController.create().start().resume().pause().destroy()

    inOrder(mockController, mockUI) {
      verify(mockController).initialize()
      verify(mockController).ui
      verify(mockUI).createView(mockController, null)
      verify(mockController).uiReady()
      verify(mockController).start()
      verify(mockController).stop()
      verify(mockController).destroy()
    }

    verifyNoMoreInteractions(mockController)
  }

  @After
  fun afterTest() {
    reset(mockUI)
    reset(mockController)
    reset(mockView)
  }

  class TestActivityControllerActivity : Activity() {
    private val callbacks = ControllerActivityCallbacks(mockController)

    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      callbacks.onCreate(this)
    }

    override fun onResume() {
      super.onResume()
      callbacks.onResume()
    }

    override fun onBackPressed() {
      if(!callbacks.onBackPressed()) super.onBackPressed()
    }

    override fun onPause() {
      super.onPause()
      callbacks.onPause()
    }

    override fun onDestroy() {
      super.onDestroy()
      callbacks.onDestroy()
    }
  }
}
