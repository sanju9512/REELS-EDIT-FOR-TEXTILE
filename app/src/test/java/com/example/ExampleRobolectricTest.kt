package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.BackgroundPreset
import com.example.model.SyncStatus
import com.example.model.UserRole
import com.example.model.VideoAnimationType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Textile Studio", appName)
  }

  @Test
  fun `verify background presets and user roles`() {
    val presets = BackgroundPreset.values()
    assertEquals(6, presets.size)

    val roles = UserRole.values()
    assertEquals(4, roles.size)

    val videoTypes = VideoAnimationType.values()
    assertEquals(4, videoTypes.size)

    val syncStatuses = SyncStatus.values()
    assertEquals(3, syncStatuses.size)
  }
}
