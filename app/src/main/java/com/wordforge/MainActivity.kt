package com.wordforge

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.wordforge.ui.navigation.NavGraph
import com.wordforge.ui.navigation.Screen
import com.wordforge.ui.theme.WordForgeTheme

class MainActivity : ComponentActivity() {

    // Launcher for requesting notification permission on Android 13+
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            // Permission result — notifications will work if granted,
            // app still functions without them if denied
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()

        // Check if we were opened from a notification with a word ID
        val wordIdFromNotification = intent.getStringExtra("wordId")

        setContent {
            WordForgeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)

                    // If opened from a notification, navigate to the quiz screen
                    if (wordIdFromNotification != null) {
                        // Clear the extra so rotation doesn't re-trigger navigation
                        intent.removeExtra("wordId")
                        navController.navigate(Screen.Quiz.createRoute(wordIdFromNotification)) {
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        // Only needed on Android 13 (API 33) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}