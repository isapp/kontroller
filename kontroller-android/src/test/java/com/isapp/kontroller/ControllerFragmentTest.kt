package com.isapp.kontroller

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nhaarman.mockito_kotlin.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.util.FragmentController

@RunWith(RobolectricTestRunner::class)
class ControllerFragmentTest {
  companion object {
    private val mockUI = mock<TestUI>()
    private val mockController = mock<TestController>()
    private val mockView = mock<View>()
  }

  private lateinit var fragmentController: FragmentController<TestFragmentControllerFragment>

  @Before
  fun beforeTest() {
    fragmentController = FragmentController.of(TestFragmentControllerFragment())
    whenever(mockController.ui).doReturn(mockUI)
    whenever(mockUI.createView(any())).doReturn(mockView)
  }

  @Test
  fun onCreateView_callsInitialize_thenCreateView_thenUIReady() {
    fragmentController.create().start()

    inOrder(mockController, mockUI) {
      verify(mockController).initialize()
      verify(mockController).ui
      verify(mockUI).createView(mockController)
      verify(mockController).uiReady()
    }

    verifyNoMoreInteractions(mockController)
  }

  @Test
  fun onCreateView_ifThereIsNoView_callsInitialize_thenCreateView_butNotUIReady() {
    whenever(mockUI.createView(any())).doReturn(null as View?)
    fragmentController.create().start()

    inOrder(mockController, mockUI) {
      verify(mockController).initialize()
      verify(mockController).ui
      verify(mockUI).createView(mockController)
      verify(mockController, never()).uiReady()
    }

    verifyNoMoreInteractions(mockController)
  }

  @Test
  fun onResume_callsStart() {
    fragmentController.create().start().resume()

    inOrder(mockController, mockUI) {
      verify(mockController).initialize()
      verify(mockController).ui
      verify(mockUI).createView(mockController)
      verify(mockController).uiReady()
      verify(mockController).start()
    }

    verifyNoMoreInteractions(mockController)
  }

  @Test
  fun onBackPressed_callsNavigateBack() {
    fragmentController.create().start().resume().get().onBackPressed()

    inOrder(mockController, mockUI) {
      verify(mockController).initialize()
      verify(mockController).ui
      verify(mockUI).createView(mockController)
      verify(mockController).uiReady()
      verify(mockController).start()
      verify(mockController).navigateBack()
    }

    verifyNoMoreInteractions(mockController)
  }

  @Test
  fun onPause_callsStop() {
    fragmentController.create().start().resume().pause()

    inOrder(mockController, mockUI) {
      verify(mockController).initialize()
      verify(mockController).ui
      verify(mockUI).createView(mockController)
      verify(mockController).uiReady()
      verify(mockController).start()
      verify(mockController).stop()
    }

    verifyNoMoreInteractions(mockController)
  }

  @Test
  fun onStop_callsNothingAfterStop() {
    fragmentController.create().start().resume().pause().stop()

    inOrder(mockController, mockUI) {
      verify(mockController).initialize()
      verify(mockController).ui
      verify(mockUI).createView(mockController)
      verify(mockController).uiReady()
      verify(mockController).start()
      verify(mockController).stop()
    }

    verifyNoMoreInteractions(mockController)
  }

  @Test
  fun onDestroy_callsDestroy() {
    fragmentController.create().start().resume().pause().destroy()

    inOrder(mockController, mockUI) {
      verify(mockController).initialize()
      verify(mockController).ui
      verify(mockUI).createView(mockController)
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

  class TestFragmentControllerFragment : Fragment() {
    private val callbacks = ControllerFragmentCallbacks(mockController)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
      return callbacks.onCreateView() ?: mockView
    }

    override fun onResume() {
      super.onResume()
      callbacks.onResume()
    }

    fun onBackPressed() = callbacks.onBackPressed()

    override fun onPause() {
      super.onPause()
      callbacks.onPause()
    }

    override fun onDestroyView() {
      super.onDestroy()
      callbacks.onDestroyView()
    }
  }
}
