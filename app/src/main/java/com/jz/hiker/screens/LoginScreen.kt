package com.jz.hiker.screens

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jz.hiker.R
import com.jz.hiker.common.AuthenticationState
import com.jz.hiker.common.ProgressSpinner
import com.jz.hiker.main.AppViewModel
import com.jz.hiker.ui.theme.DarkGrey40
import com.jz.hiker.ui.theme.HikerTheme
import com.jz.hiker.ui.theme.Typography

@Composable
fun LoginScreen(viewModel: AppViewModel) {
    val context = LocalContext.current
    val toastMessage = viewModel.toastMessage

    // toast message for errors
    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }
    Box(
        modifier = Modifier
            .background(DarkGrey40)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    )
    {
        when(viewModel.authState) {
            AuthenticationState.Unauthorized -> LoginContent(viewModel)
            AuthenticationState.WaitingForAuth -> ProgressSpinner(Modifier.align(Alignment.Center)) {}
            AuthenticationState.Authorized -> StatsScreen(viewModel)
        }
    }
}

@Composable
fun LoginContent(viewModel: AppViewModel) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            style = Typography.headlineLarge,
            text = "Login",
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.size(50.dp))
        LoginButton { viewModel.loginStravaAccount() }
    }
}

@Composable
fun LoginButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val buttonPressed by interactionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (buttonPressed) 0.95f else 1.0f, label = "click"
    )

    Icon(
        painter = painterResource(id = R.drawable.strava),
        tint = Color.Unspecified,
        contentDescription = "strava icon",
        modifier = Modifier
            .size(150.dp)
            .scale(buttonScale)
            .shadow(12.dp, RoundedCornerShape(4.dp), false)
            .clip(RoundedCornerShape(4.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
    )
}