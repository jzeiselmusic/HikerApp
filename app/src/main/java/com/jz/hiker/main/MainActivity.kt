package com.jz.hiker.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.jz.hiker.screens.LoginScreen

class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appViewModel.initConnectionManager(context = this)
        enableEdgeToEdge()
        setContent {
            SetScreen(appViewModel)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        appViewModel.handleOAuthRedirect(intent)
    }

    @Composable
    fun SetScreen(appViewModel: AppViewModel) {
        LoginScreen(appViewModel)
    }
}
