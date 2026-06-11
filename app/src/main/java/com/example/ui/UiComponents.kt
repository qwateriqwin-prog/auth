package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.SolidColor
import com.example.db.AuthenticatorAccount
import com.example.util.CryptoHelper
import com.example.util.TotpHelper
import com.example.viewmodel.AuthenticatorViewModel

val CopyIcon: ImageVector
    get() = ImageVector.Builder(
        name = "ContentCopy",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFFD0BCFF))) {
            moveTo(16f, 1f)
            lineTo(4f, 1f)
            curveTo(2.9f, 1f, 2f, 1.9f, 2f, 3f)
            verticalLineTo(17f)
            horizontalLineTo(4f)
            verticalLineTo(3f)
            horizontalLineTo(16f)
            verticalLineTo(1f)
            close()
            moveTo(19f, 5f)
            horizontalLineTo(8f)
            curveTo(6.9f, 5f, 6f, 5.9f, 6f, 7f)
            verticalLineTo(21f)
            curveTo(6f, 22.1f, 6.9f, 23f, 8f, 23f)
            horizontalLineTo(19f)
            curveTo(20.1f, 23f, 21f, 22.1f, 21f, 21f)
            verticalLineTo(7f)
            curveTo(21f, 5.9f, 20.1f, 5f, 19f, 5f)
            close()
            moveTo(19f, 21f)
            horizontalLineTo(8f)
            verticalLineTo(7f)
            horizontalLineTo(19f)
            verticalLineTo(21f)
            close()
        }
    }.build()

// Cozy theme color descriptors mapped to the design themes (0 = Elegant Dark, 1 = AMOLED Pure Black, 2 = Light Mode)
var currentThemeActive = 0

val CyberSlateBg: Color
    get() = when (currentThemeActive) {
        1 -> Color(0xFF000000)
        2 -> Color(0xFFF4F6F9) // Cozy Light Blue-Grey Background
        else -> Color(0xFF1C1B1F)
    }

val CyberCardSurface: Color
    get() = when (currentThemeActive) {
        1 -> Color(0xFF121212)
        2 -> Color(0xFFFFFFFF) // Crisp Clean White Card
        else -> Color(0xFF2B2930)
    }

val CyberPrimaryTeal: Color
    get() = when (currentThemeActive) {
        1 -> Color(0xFFBB86FC)
        2 -> Color(0xFF6200EE) // Solid Material Purple/Indigo for top-notch light contrast
        else -> Color(0xFFD0BCFF)
    }

val CyberGold: Color
    get() = when (currentThemeActive) {
        2 -> Color(0xFFB00020) // Deep red warning text for Light Mode
        else -> Color(0xFFE57373)
    }

val LightSlateText: Color
    get() = when (currentThemeActive) {
        1 -> Color(0xFFFFFFFF)
        2 -> Color(0xFF1C1B1F) // Deep Slate Charcoal for readability
        else -> Color(0xFFE6E1E5)
    }

val SoftGreySub: Color
    get() = when (currentThemeActive) {
        1 -> Color(0xFFB0B0B0)
        2 -> Color(0xFF5A5A5A) // High contrast medium-dark grey
        else -> Color(0xFFCAC4D0)
    }

val DarkBorderColor: Color
    get() = when (currentThemeActive) {
        1 -> Color(0xFF262626)
        2 -> Color(0xFFE0E0E0) // Subtle borders
        else -> Color(0xFF49454F)
    }

val SecureBannerBg: Color
    get() = when (currentThemeActive) {
        1 -> Color(0xFF1C1B1F)
        2 -> Color(0xFFEADDFF) // Beautiful modern soft lavender
        else -> Color(0xFF332D41)
    }

val TextOnPrimary: Color
    get() = when (currentThemeActive) {
        1 -> Color(0xFF121212)
        2 -> Color(0xFFFFFFFF) // High contrast pure white text
        else -> Color(0xFF381E72)
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticatorAppContent(viewModel: AuthenticatorViewModel) {
    val themeType by viewModel.darkThemeType.collectAsState()
    currentThemeActive = themeType

    val context = LocalContext.current
    val isLocked by viewModel.isLocked.collectAsState()
    val isPinSet by viewModel.isPinSet.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val timeSeconds by viewModel.currentTimeSeconds.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showSplashScreen by remember { mutableStateOf(true) }

    // Listener for viewmodel toast/status messages
    LaunchedEffect(Unit) {
        viewModel.statusMessage.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }

    // Force Arabic/RTL layout for a specialized Arabic native visual app
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = CyberSlateBg
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (showSplashScreen) {
                    SplashScreenView(onFinish = { showSplashScreen = false })
                } else if (isLocked) {
                    LockScreenView(
                        isPinSet = isPinSet,
                        onPinEntered = { pin ->
                            val success = viewModel.verifyPinCode(pin)
                            if (!success) {
                                Toast.makeText(context, "رمز الحماية غير صحيح!", Toast.LENGTH_SHORT).show()
                            }
                            success
                        },
                        onSetupPin = { pin ->
                            viewModel.setAppPinCode(pin)
                        }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = CyberSlateBg,
                        topBar = {
                            TopAppBar(
                                title = {
                                    Column {
                                        Text(
                                            text = "المصادق الآمن",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LightSlateText
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .background(CyberPrimaryTeal, CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "تشفير تام AES-GCM",
                                                fontSize = 11.sp,
                                                color = CyberPrimaryTeal,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                },
                                actions = {
                                    IconButton(
                                        onClick = { showSettingsDialog = true },
                                        modifier = Modifier.testTag("action_settings")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = "الاعدادات والأمان",
                                            tint = LightSlateText
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = CyberSlateBg,
                                    titleContentColor = LightSlateText
                                )
                            )
                        },
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = { showAddDialog = true },
                                containerColor = CyberPrimaryTeal,
                                contentColor = TextOnPrimary,
                                modifier = Modifier
                                    .padding(bottom = 16.dp)
                                    .testTag("fab_add_account"),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("إضافة حساب", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(horizontal = 16.dp)
                        ) {
                            // High gloss design banner explaining encryption
                            BannerSecureStatus()

                            Spacer(modifier = Modifier.height(12.dp))

                            // Custom designed search box
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("search_field"),
                                placeholder = {
                                    Text(
                                        "ابحث عن حساب (مثل Gmail)...",
                                        color = SoftGreySub,
                                        fontSize = 14.sp
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "بحث",
                                        tint = SoftGreySub
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "مسح",
                                                tint = SoftGreySub
                                            )
                                        }
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = CyberCardSurface,
                                    unfocusedContainerColor = CyberCardSurface,
                                    focusedBorderColor = CyberPrimaryTeal,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTextColor = LightSlateText,
                                    unfocusedTextColor = LightSlateText
                                ),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            val filteredAccounts = accounts.filter {
                                it.label.contains(searchQuery, ignoreCase = true) ||
                                        it.issuer.contains(searchQuery, ignoreCase = true)
                            }

                            if (filteredAccounts.isEmpty()) {
                                EmptyAccountsState(searchActive = searchQuery.isNotEmpty())
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(filteredAccounts, key = { it.id }) { account ->
                                        AuthenticatorCard(
                                            account = account,
                                            currentTimeSeconds = timeSeconds,
                                            onCopy = { code ->
                                                val clipboard =
                                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText("Authenticator Code", code)
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(
                                                    context,
                                                    "تم نسخ الرمز ${code.take(3)} ${code.drop(3)} بنجاح!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            onDelete = {
                                                viewModel.deleteAccount(account.id)
                                            },
                                            onEdit = { label, issuer ->
                                                viewModel.updateAccountInfo(account.id, label, issuer)
                                            }
                                        )
                                    }
                                    // Space for floating action button overlap
                                    item {
                                        Spacer(modifier = Modifier.height(100.dp))
                                    }
                                }
                            }
                        }
                    }

                    if (showAddDialog) {
                        AddAccountDialog(
                            currentTimeSeconds = timeSeconds,
                            onDismiss = { showAddDialog = false },
                            onConfirm = { label, issuer, secret ->
                                val ok = viewModel.addAccount(label, issuer, secret)
                                if (ok) showAddDialog = false
                            }
                        )
                    }

                    if (showSettingsDialog) {
                        SettingsDialogContent(
                            viewModel = viewModel,
                            onDismiss = { showSettingsDialog = false }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BannerSecureStatus() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = SecureBannerBg),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorderColor, DarkBorderColor.copy(0.3f))))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(CyberPrimaryTeal.copy(0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "قفل",
                    tint = CyberPrimaryTeal,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "حماية الرموز نشطة بالتشفير التام",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = LightSlateText
                )
                Text(
                    text = "جميع المفاتيح السرية مشفرة بـ AES-256-GCM باستخدام مفتاح أمان عتادي محمي داخل الـ Entry KeyStore للهاتف.",
                    fontSize = 11.sp,
                    color = SoftGreySub,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun EmptyAccountsState(searchActive: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(CyberCardSurface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "?",
                tint = SoftGreySub,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (searchActive) "لا توجد نتائج بحث مطابقة" else "تطبيق المصادقة فارغ",
            fontWeight = FontWeight.Bold,
            color = LightSlateText,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (searchActive) "تأكد من كتابة البريد الإلكتروني أو اسم الخدمة بشكل صحيح."
            else "انقر على زر 'إضافة حساب' في الأسفل لجلب مفتاح Gmail الخاص بك وتأمين حساباتك.",
            fontSize = 13.sp,
            color = SoftGreySub,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
            lineHeight = 20.sp
        )
    }
}

@Composable
fun AuthenticatorCard(
    account: AuthenticatorAccount,
    currentTimeSeconds: Long,
    onCopy: (String) -> Unit,
    onDelete: () -> Unit,
    onEdit: (label: String, issuer: String) -> Unit
) {
    val decryptedSecret = remember(account.encryptedSecret) {
        CryptoHelper.decrypt(account.encryptedSecret)
    }

    val otpCode = remember(decryptedSecret, currentTimeSeconds) {
        TotpHelper.generateTotp(decryptedSecret, currentTimeSeconds, account.period)
    }

    val secondsRemaining = account.period - (currentTimeSeconds % account.period)
    val fractionRemaining = secondsRemaining.toFloat() / account.period

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCopy(otpCode) }
            .testTag("account_card_${account.id}"),
        colors = CardDefaults.cardColors(containerColor = CyberCardSurface),
        shape = RoundedCornerShape(24.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorderColor.copy(0.3f), DarkBorderColor.copy(0.1f)))),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Visual Brand representation
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                getBrandColor(account.issuer),
                                getBrandColor(account.issuer).copy(0.6f)
                            )
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                val initials = account.issuer.take(2).uppercase()
                Text(
                    text = initials,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text Label and Email
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = account.issuer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = LightSlateText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(0.1f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "رمز TOTP",
                            fontSize = 9.sp,
                            color = SoftGreySub,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = account.label,
                    fontSize = 12.sp,
                    color = SoftGreySub,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Beautiful 6 Digit OTP formatted cleanly e.g., 235 481
                val formattedOtp = if (otpCode.length == 6) {
                    "${otpCode.take(3)}   ${otpCode.drop(3)}"
                } else otpCode

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = formattedOtp,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (secondsRemaining <= 5) CyberGold else CyberPrimaryTeal,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp,
                        modifier = Modifier.testTag("otp_code_text_${account.id}")
                    )
                    
                    IconButton(
                        onClick = { onCopy(otpCode) },
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.White.copy(0.08f), RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = CopyIcon,
                            contentDescription = "نسخ الكود",
                            tint = CyberPrimaryTeal,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(0.95f)
                ) {
                    androidx.compose.material3.LinearProgressIndicator(
                        progress = { fractionRemaining },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (secondsRemaining <= 5) CyberGold else CyberPrimaryTeal,
                        trackColor = Color.White.copy(0.12f)
                    )
                    Text(
                        text = "$secondsRemaining ثانية",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (secondsRemaining <= 5) CyberGold else SoftGreySub
                    )
                }
            }

            // Visual Timer + Action controls
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                // Circular 30s Countdown
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(36.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { fractionRemaining },
                        modifier = Modifier.size(28.dp),
                        color = if (secondsRemaining <= 5) CyberGold else CyberPrimaryTeal,
                        strokeWidth = 3.dp,
                        trackColor = Color.White.copy(0.1f)
                    )
                    Text(
                        text = secondsRemaining.toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (secondsRemaining <= 5) CyberGold else LightSlateText
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row {
                    IconButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "تعديل",
                            tint = SoftGreySub,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف",
                            tint = Color.Red.copy(0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        var editLabel by remember { mutableStateOf(account.label) }
        var editIssuer by remember { mutableStateOf(account.issuer) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("تعديل معلومات الحساب", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editIssuer,
                        onValueChange = { editIssuer = it },
                        label = { Text("الجهة / الموقع (مثلاً Gmail)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = LightSlateText,
                            unfocusedTextColor = LightSlateText
                        ),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editLabel,
                        onValueChange = { editLabel = it },
                        label = { Text("عنوان الحساب (البريد الإلكتروني)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = LightSlateText,
                            unfocusedTextColor = LightSlateText
                        ),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onEdit(editLabel, editIssuer)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimaryTeal)
                ) {
                    Text("حفظ التغييرات", color = CyberSlateBg)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("إلغاء", color = SoftGreySub)
                }
            },
            containerColor = CyberCardSurface,
            textContentColor = LightSlateText,
            titleContentColor = LightSlateText
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("حذف حساب المصادقة؟", fontWeight = FontWeight.Bold, color = Color.Red.copy(0.9f)) },
            text = {
                Text(
                    "هل أنت متأكد من حذف حساب (${account.issuer} - ${account.label})؟\nلا يمكنك التراجع عن هذا الإجراء وسيتم مسح المفتاح السري نهائياً.",
                    color = LightSlateText
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("تأكيد الحذف", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("إلغاء", color = SoftGreySub)
                }
            },
            containerColor = CyberCardSurface,
            titleContentColor = LightSlateText
        )
    }
}

private fun getBrandColor(issuer: String): Color {
    val name = issuer.lowercase()
    return when {
        name.contains("gmail") || name.contains("google") -> Color(0xFFEA4335)
        name.contains("outlook") || name.contains("microsoft") -> Color(0xFF0078D4)
        name.contains("facebook") || name.contains("meta") -> Color(0xFF1877F2)
        name.contains("github") -> Color(0xFF24292E)
        name.contains("discord") -> Color(0xFF5865F2)
        name.contains("yahoo") -> Color(0xFF6001D2)
        else -> Color(0xFF14B8A6) // default generic brand teal
    }
}

@Composable
fun AddAccountDialog(
    currentTimeSeconds: Long,
    onDismiss: () -> Unit,
    onConfirm: (label: String, issuer: String, secret: String) -> Unit
) {
    var tabSelected by remember { mutableStateOf(0) } // 0 = Paste link/URI, 1 = Manual Key Code

    var inputUri by remember { mutableStateOf("") }
    var inputLabel by remember { mutableStateOf("") }
    var inputIssuer by remember { mutableStateOf("") }
    var inputSecret by remember { mutableStateOf("") }

    val context = LocalContext.current
    val clipboard = remember { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
    var clipboardContent by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val clip = clipboard.primaryClip
            if (clip != null && clip.itemCount > 0) {
                val text = clip.getItemAt(0).text?.toString()?.trim() ?: ""
                if (text.isNotEmpty()) {
                    clipboardContent = text
                }
            }
        } catch (e: Exception) {
            // Ignore clipboard errors gracefully
        }
    }

    val isClipUri = clipboardContent.startsWith("otpauth://", ignoreCase = true)
    val isClipBase32 = remember(clipboardContent) {
        val clean = clipboardContent.replace(" ", "").replace("-", "")
        clean.isNotEmpty() && com.example.util.Base32.isValidBase32(clean) && !isClipUri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "إضافة مفتاح مصادقة جديد",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Clipboard detection notification banner
                if (clipboardContent.isNotEmpty() && (isClipUri || isClipBase32)) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberPrimaryTeal.copy(0.12f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "تم العثور على مفتاح في الحافظة!",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberPrimaryTeal
                                )
                                Text(
                                    text = if (isClipUri) "رابط إعداد OTP كامل" else "مفتاح سري (Base32)",
                                    fontSize = 10.sp,
                                    color = SoftGreySub
                                )
                            }
                            
                            Button(
                                onClick = {
                                    if (isClipUri) {
                                        tabSelected = 0
                                        inputUri = clipboardContent
                                    } else {
                                        tabSelected = 1
                                        inputSecret = clipboardContent
                                        inputIssuer = "حساب مستورد"
                                        inputLabel = "مستورد من الحافظة"
                                    }
                                    clipboardContent = "" // clear banner
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimaryTeal),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp),
                                modifier = Modifier.height(28.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("تعبئة تلقائية", fontSize = 10.sp, color = CyberSlateBg, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Interactive Tab Picker
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberSlateBg, RoundedCornerShape(8.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (tabSelected == 0) CyberPrimaryTeal else Color.Transparent)
                            .clickable { tabSelected = 0 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "نسخ رابط otpauth",
                            color = if (tabSelected == 0) CyberSlateBg else SoftGreySub,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (tabSelected == 1) CyberPrimaryTeal else Color.Transparent)
                            .clickable { tabSelected = 1 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "إدخال يدوي للمفتاح",
                            color = if (tabSelected == 1) CyberSlateBg else SoftGreySub,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                if (tabSelected == 0) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "رابط الإعداد الذاتي (otpauth URI)",
                            fontSize = 11.sp,
                            color = CyberPrimaryTeal,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(
                            value = inputUri,
                            onValueChange = { inputUri = it },
                            placeholder = { Text("الصق الرابط الكامل هنا (يبدأ بـ otpauth://)", fontSize = 13.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("uri_input_field"),
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = LightSlateText,
                                unfocusedTextColor = LightSlateText
                            )
                        )
                        
                        // Parse URI and display live preview if valid
                        val parsedUri = remember(inputUri) { TotpHelper.parseOtpAuthUri(inputUri.trim()) }
                        val rawBase32FromUri = remember(inputUri) {
                            val clean = inputUri.replace(" ", "").replace("-", "").trim()
                            if (com.example.util.Base32.isValidBase32(clean)) clean else null
                        }

                        if (parsedUri != null) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CyberPrimaryTeal.copy(0.12f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = "Valid", tint = CyberPrimaryTeal, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("رابط مصادقة صالح!", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CyberPrimaryTeal)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("الخدمة: ${parsedUri.issuer}", color = LightSlateText, fontSize = 11.sp)
                                    Text("الحساب: ${parsedUri.label}", color = LightSlateText, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    val liveOtp = remember(parsedUri, currentTimeSeconds) {
                                        TotpHelper.generateTotp(parsedUri.secret, currentTimeSeconds, parsedUri.period)
                                    }
                                    val formattedLiveOtp = if (liveOtp.length == 6) "${liveOtp.take(3)}   ${liveOtp.drop(3)}" else liveOtp
                                    
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column {
                                            Text("رمز المصادقة المؤقت للنسخ:", fontSize = 10.sp, color = SoftGreySub)
                                            Text(
                                                text = formattedLiveOtp,
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CyberPrimaryTeal,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                        
                                        Button(
                                            onClick = {
                                                val clipMgr = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText("2FA Code", liveOtp)
                                                clipMgr.setPrimaryClip(clip)
                                                Toast.makeText(context, "تم نسخ الرمز $liveOtp", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = CyberPrimaryTeal),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp),
                                            modifier = Modifier.height(32.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("نسخ سريع", fontSize = 11.sp, color = CyberSlateBg, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        } else if (rawBase32FromUri != null) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CyberPrimaryTeal.copy(0.12f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = "Valid", tint = CyberPrimaryTeal, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("مفتاح سري صالح (Base32 Key)!", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CyberPrimaryTeal)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("ملاحظة: لقد أدخلت مفتاحاً سرياً وخلقنا الرمز له مباشرة.", color = SoftGreySub, fontSize = 10.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    val liveOtp = remember(rawBase32FromUri, currentTimeSeconds) {
                                        TotpHelper.generateTotp(rawBase32FromUri, currentTimeSeconds, 30)
                                    }
                                    val formattedLiveOtp = if (liveOtp.length == 6) "${liveOtp.take(3)}   ${liveOtp.drop(3)}" else liveOtp
                                    
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column {
                                            Text("رمز المصادقة لكود الحساب:", fontSize = 10.sp, color = SoftGreySub)
                                            Text(
                                                text = formattedLiveOtp,
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CyberPrimaryTeal,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                        
                                        Button(
                                            onClick = {
                                                val clipMgr = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText("2FA Code", liveOtp)
                                                clipMgr.setPrimaryClip(clip)
                                                Toast.makeText(context, "تم نسخ الرمز $liveOtp", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = CyberPrimaryTeal),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp),
                                            modifier = Modifier.height(32.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("نسخ سريع", fontSize = 11.sp, color = CyberSlateBg, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        } else if (inputUri.isNotEmpty()) {
                            Text(
                                "تنبيه: الرابط المدخل ليس رمز otpauth صالحاً وليس مفتاح Base32 صحيحاً.",
                                color = CyberGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        } else {
                            Text(
                                "هذا الخيار مثالي إذا قمت بنسخ رابط الإعداد المباشر الموفر من حسابك عند تفعيل الميزة.",
                                fontSize = 10.sp,
                                color = SoftGreySub,
                                lineHeight = 14.sp
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = inputIssuer,
                            onValueChange = { inputIssuer = it },
                            label = { Text("الموقع أو الخدمة (مثلاً Gmail)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("manual_issuer_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = LightSlateText,
                                unfocusedTextColor = LightSlateText
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = inputLabel,
                            onValueChange = { inputLabel = it },
                            label = { Text("عنوان الحساب (البريد الإلكتروني للـ Gmail)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("manual_label_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = LightSlateText,
                                unfocusedTextColor = LightSlateText
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = inputSecret,
                            onValueChange = { inputSecret = it },
                            label = { Text("مفتاح الخدمة السري (Base32 Key)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("manual_secret_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = LightSlateText,
                                unfocusedTextColor = LightSlateText
                            ),
                            singleLine = true
                        )

                        TextButton(
                            onClick = {
                                inputIssuer = "Google"
                                inputLabel = "zozmmnosh@gmail.com"
                                inputSecret = "6ccp tock cj7r qrb2 i4so dj2c 6zfr cqie"
                            },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Text(
                                "💡 اضغط هنا لتعبئة بريدك الإلكتروني ومفتاحك التجريبي تلقائياً",
                                fontSize = 11.sp,
                                color = CyberPrimaryTeal,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        val cleanSecret = inputSecret.replace(" ", "").replace("-", "")
                        val isSecretValid = remember(cleanSecret) {
                            cleanSecret.isNotEmpty() && com.example.util.Base32.isValidBase32(cleanSecret)
                        }

                        if (isSecretValid) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CyberPrimaryTeal.copy(0.12f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = "Valid", tint = CyberPrimaryTeal, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("المفتاح السري صالح وجاهز!", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CyberPrimaryTeal)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    val liveOtp = remember(cleanSecret, currentTimeSeconds) {
                                        TotpHelper.generateTotp(cleanSecret, currentTimeSeconds, 30)
                                    }
                                    val formattedLiveOtp = if (liveOtp.length == 6) "${liveOtp.take(3)}   ${liveOtp.drop(3)}" else liveOtp
                                    
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column {
                                            Text("رمز المصادقة المولد من هذا المفتاح:", fontSize = 10.sp, color = SoftGreySub)
                                            Text(
                                                text = formattedLiveOtp,
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CyberPrimaryTeal,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                        
                                        Button(
                                            onClick = {
                                                val clipMgr = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText("2FA Code", liveOtp)
                                                clipMgr.setPrimaryClip(clip)
                                                Toast.makeText(context, "تم نسخ الرمز $liveOtp", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = CyberPrimaryTeal),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp),
                                            modifier = Modifier.height(32.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("نسخ سريع", fontSize = 11.sp, color = CyberSlateBg, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        } else if (inputSecret.isNotEmpty()) {
                            Text(
                                text = "تنبيه: الرمز السري المدخل غير صالح كـ Base32. تأكد من إزالة الأحرف الخاطئة.",
                                color = CyberGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        } else {
                            Text(
                                "المفتاح السري هو الرمز المكون من حروف وأرقام (مثل JBSW...3P) الممنوح من الخدمة التي تود تأمينها.",
                                fontSize = 10.sp,
                                color = SoftGreySub,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (tabSelected == 0) {
                        onConfirm("", "", inputUri)
                    } else {
                        onConfirm(inputLabel, inputIssuer, inputSecret)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimaryTeal),
                modifier = Modifier.testTag("confirm_add_button")
            ) {
                Text("إضافة الحساب", color = CyberSlateBg, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = SoftGreySub)
            }
        },
        containerColor = CyberCardSurface,
        titleContentColor = LightSlateText,
        textContentColor = LightSlateText
    )
}

@Composable
fun SettingsDialogContent(
    viewModel: AuthenticatorViewModel,
    onDismiss: () -> Unit
) {
    val isPinSet by viewModel.isPinSet.collectAsState()
    var pinValue by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إعدادات قفل وحماية التطبيق", fontWeight = FontWeight.Bold, color = LightSlateText) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("قفل الشاشة النشط", fontWeight = FontWeight.Bold, color = LightSlateText, fontSize = 14.sp)
                        Text(
                            "يتطلب إدخال رمز الأمان الشخصي فور فتح التطبيق لحماية مفاتيحك من المتطفلين.",
                            color = SoftGreySub,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                    Switch(
                        checked = isPinSet,
                        onCheckedChange = { enable ->
                            if (!enable) {
                                viewModel.disableAppLock()
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberSlateBg,
                            checkedTrackColor = CyberPrimaryTeal,
                            uncheckedThumbColor = SoftGreySub,
                            uncheckedTrackColor = CyberSlateBg
                        ),
                        modifier = Modifier.testTag("app_lock_switch")
                    )
                }

                HorizontalDivider(color = Color.White.copy(0.1f))

                // Theme Selection Options
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("نمط مظهر التطبيق (Theme Mode)", fontWeight = FontWeight.Bold, color = LightSlateText, fontSize = 14.sp)
                    Text(
                        "اختر المظهر الذي يوفر أفضل راحة لعينك ويسهل عليك إدارة حساباتك في مختلف البيئات.",
                        color = SoftGreySub,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                    
                    val activeTheme by viewModel.darkThemeType.collectAsState()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Elegant Dark Card Option
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (activeTheme == 0) CyberPrimaryTeal.copy(0.15f) else Color.White.copy(0.04f),
                                    RoundedCornerShape(10.dp)
                                )
                                .border(
                                    1.dp,
                                    if (activeTheme == 0) CyberPrimaryTeal else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.updateDarkThemeType(0) }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "الليلي الأنيق",
                                    color = if (activeTheme == 0) CyberPrimaryTeal else LightSlateText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text("رمادي للراحة", color = SoftGreySub, fontSize = 9.sp)
                            }
                        }

                        // AMOLED Black Card Option
                        Box(
                            modifier = Modifier
                                .weight(1.1f)
                                .background(
                                    if (activeTheme == 1) CyberPrimaryTeal.copy(0.15f) else Color.White.copy(0.04f),
                                    RoundedCornerShape(10.dp)
                                )
                                .border(
                                    1.dp,
                                    if (activeTheme == 1) CyberPrimaryTeal else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.updateDarkThemeType(1) }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "الليلي AMOLED",
                                    color = if (activeTheme == 1) CyberPrimaryTeal else LightSlateText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text("توفير طاقة فائق", color = SoftGreySub, fontSize = 9.sp)
                            }
                        }

                        // Light Theme Option
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (activeTheme == 2) CyberPrimaryTeal.copy(0.15f) else Color.White.copy(0.04f),
                                    RoundedCornerShape(10.dp)
                                )
                                .border(
                                    1.dp,
                                    if (activeTheme == 2) CyberPrimaryTeal else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.updateDarkThemeType(2) }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "الوضع الفاتح",
                                    color = if (activeTheme == 2) CyberPrimaryTeal else LightSlateText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text("مظهر ناصع", color = SoftGreySub, fontSize = 9.sp)
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color.White.copy(0.1f))

                if (!isPinSet) {
                    // Lock creation inputs
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "تعيين رمز أمان جديد (PIN):",
                            fontSize = 13.sp,
                            color = CyberPrimaryTeal,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(
                            value = pinValue,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() } && it.length <= 8) {
                                    pinValue = it
                                    pinError = false
                                }
                            },
                            label = { Text("أدخل رمز PIN الحماية (أرقام فقط)") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pin_setup_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = LightSlateText,
                                unfocusedTextColor = LightSlateText
                            ),
                            singleLine = true
                        )
                        if (pinError) {
                            Text("الرمز يجب أن يكون 4 أرقام على الأقل لضمان الحماية.", color = Color.Red, fontSize = 11.sp)
                        }
                        Button(
                            onClick = {
                                if (pinValue.length >= 4) {
                                    viewModel.setAppPinCode(pinValue)
                                    pinValue = ""
                                } else {
                                    pinError = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_pin_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberPrimaryTeal)
                        ) {
                            Text("تطبيق رمز الحماية", color = CyberSlateBg, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberPrimaryTeal.copy(0.1f), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "قفل", tint = CyberPrimaryTeal)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تم تفعيل القفل وحماية تشفير البيانات بنجاح.",
                            color = LightSlateText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                HorizontalDivider(color = Color.White.copy(0.1f))

                // Crypto breakdown details
                Column {
                    Text("معلومات خوارزمية التشفير:", fontWeight = FontWeight.Bold, color = LightSlateText, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "يقوم التطبيق بحفظ المفتاح السري لكل حساب في قاعدة بيانات محلية مشفرة بكلمة مرور مشتقة بالكامل من Keystore Android. " +
                                "الرموز تُعرض وتُحسب وتتحلل فورياً في الذاكرة المؤقتة لحماية قصوى ولا يتم رفع أي شيء لخدام خارجي.",
                        color = SoftGreySub,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        textAlign = TextAlign.Justify
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimaryTeal)
            ) {
                Text("إغلاق", color = CyberSlateBg)
            }
        },
        containerColor = CyberCardSurface,
        titleContentColor = LightSlateText,
        textContentColor = LightSlateText
    )
}

@Composable
fun LockScreenView(
    isPinSet: Boolean,
    onPinEntered: (String) -> Boolean,
    onSetupPin: (String) -> Unit
) {
    var pinText by remember { mutableStateOf("") }
    var pinSetupState by remember { mutableStateOf(0) } // 0 = Enter Pin, 1 = Setup First Pin (if not set)
    var firstPinAttempt by remember { mutableStateOf("") }

    LaunchedEffect(isPinSet) {
        if (!isPinSet) {
            pinSetupState = 1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberSlateBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "قفل",
            tint = CyberPrimaryTeal,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "المصادق الآمن",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = LightSlateText,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))

        val title = when {
            !isPinSet && pinSetupState == 1 && firstPinAttempt.isEmpty() -> "إعداد رمز المصادقة لأول مرة"
            !isPinSet && pinSetupState == 1 -> "تأكيد الرمز السري الجديد"
            else -> "تطبيق المصادق مقفل"
        }

        val subtitle = when {
            !isPinSet && pinSetupState == 1 && firstPinAttempt.isEmpty() -> "تفضل بإدخال رمز PIN جديد مكون من 4 أرقام على الأقل للتحكم في حساباتك."
            !isPinSet && pinSetupState == 1 -> "أدخل رمز PIN عينه للتأكيد والتحقق."
            else -> "الرجاء إدخال رمز الأمان السري الشخصي لفتح واستخدام التطبيق."
        }

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = CyberPrimaryTeal,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = SoftGreySub,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Screen indicators for numbers entered
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(24.dp)
        ) {
            val indicatorCount = if (pinText.isEmpty()) 0 else pinText.length
            for (i in 0 until 4) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(
                            color = if (i < indicatorCount) CyberPrimaryTeal else Color.White.copy(0.15f),
                            shape = CircleShape
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Keypad grid
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("مسح", "0", "موافق")
        )

        val onKeyPress: (String) -> Unit = { key ->
            when (key) {
                "مسح" -> {
                    if (pinText.isNotEmpty()) {
                        pinText = pinText.dropLast(1)
                    }
                }
                "موافق" -> {
                    if (pinText.length >= 4) {
                        if (!isPinSet) {
                            if (firstPinAttempt.isEmpty()) {
                                firstPinAttempt = pinText
                                pinText = ""
                            } else {
                                if (pinText == firstPinAttempt) {
                                    onSetupPin(pinText)
                                    pinText = ""
                                    firstPinAttempt = ""
                                } else {
                                    pinText = ""
                                    firstPinAttempt = ""
                                    // reset setup
                                }
                            }
                        } else {
                            val ok = onPinEntered(pinText)
                            if (!ok) {
                                pinText = ""
                            }
                        }
                    }
                }
                else -> {
                    if (pinText.length < 8) {
                        pinText += key
                    }
                }
            }
        }

        keys.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { digit ->
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(CyberCardSurface, CircleShape)
                            .clickable { onKeyPress(digit) }
                            .testTag("keypad_$digit"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = digit,
                            color = if (digit == "موافق") CyberPrimaryTeal else if (digit == "مسح") Color.Red.copy(0.8f) else LightSlateText,
                            fontSize = if (digit.all { it.isDigit() }) 24.sp else 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
