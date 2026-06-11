package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.Base32
import com.example.util.TotpHelper
import com.example.util.CryptoHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
    assertEquals("المصادق الآمن", appName)
  }

  @Test
  fun `verify base32 decoding`() {
    val decoded = Base32.decode("JBSWY3DPEHPK3PXP") // "Hello!" in Base32
    val resultString = String(decoded, Charsets.UTF_8)
    assertEquals("Hello!", resultString)
  }

  @Test
  fun `verify TOTP generation is numeric 6 digits`() {
    val otp = TotpHelper.generateTotp("JBSWY3DPEHPK3PXP")
    assertEquals(6, otp.length)
    assertTrue(otp.all { it.isDigit() })
  }

  @Test
  fun `verify hardware database encryption fallback`() {
    val plain = "SecretGoogleAccountPasswordCode102"
    val encrypted = CryptoHelper.encrypt(plain)
    val decrypted = CryptoHelper.decrypt(encrypted)
    assertEquals(plain, decrypted)
  }
}
