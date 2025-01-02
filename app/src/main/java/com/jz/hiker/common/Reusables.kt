package com.jz.hiker.common

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jz.hiker.R
import com.jz.hiker.strava.Activity
import com.jz.hiker.ui.theme.LightGrey40
import com.jz.hiker.ui.theme.Typography
import java.util.Locale

@Composable
fun BackButton(modifier: Modifier, onBack: () -> Unit) {
    Icon(
        painter = painterResource(id = R.drawable.go_next_svgrepo_com),
        tint = Color.White,
        contentDescription = "Back",
        modifier =
        modifier.size(28.dp)
            .clickable { onBack() }
            .rotate(180f)
    )
}

@Composable
fun ProgressSpinner(modifier: Modifier, onTimeout: () -> Unit) {
    // TODO: handle what to do on Spinner timeout
    CircularProgressIndicator(
        modifier = modifier.size(70.dp),
        color = LightGrey40,
        strokeWidth = 8.dp
    )
}

@Composable
fun isPortrait(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.orientation == Configuration.ORIENTATION_PORTRAIT
}

@Composable
fun RoundedBox(
    data: Activity,
    modifier: Modifier = Modifier,
    index: Int,
    currentExpanded: Long?,
    onExpand: (Long) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    var expanded = false
    currentExpanded?.let {
        expanded = currentExpanded == data.id
    }
    Box(
        modifier = modifier
            .animateContentSize()
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(16.dp))
            .height(
                if (expanded)
                    if (isPortrait)
                        HikerConfig.expandedHeightPortrait
                    else
                        HikerConfig.expandedHeightLandscape
                else
                    if (isPortrait)
                        HikerConfig.initialHeightPortrait
                    else
                        HikerConfig.initialHeightLandscape
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onExpand(data.id!!)
            }
    ) {
        Image(
            painter = pickImage(data),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier =
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .height(170.dp)
                    .blur(1.dp),
        )
        if (!expanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.35f),
                                Color.Black.copy(alpha = 0.3f)
                            )
                        )
                    )
                    .clip(RoundedCornerShape(16.dp))
            )
            Column(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text(
                    text = data.name!!,
                    color = Color(0xFFB8DDC5),
                    style = Typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            Column(
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.go_next_svgrepo_com),
                    tint = Color.White,
                    contentDescription = "go",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(70.dp)
                        .padding(horizontal = 16.dp)
                        .rotate(0f)
                )
            }
        }
        else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.25f),
                                Color.Black.copy(alpha = 0.25f)
                            )
                        )
                    )
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Text(
                    text = getText(data, index),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = Typography.headlineSmall,
                    modifier = Modifier.padding(16.dp).align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun pickImage(activity: Activity): Painter {
    return when (activity.type) {
        "Ride" -> painterResource(R.drawable.bike_new)
        "Hike" -> painterResource(R.drawable.hike_new)
        "Walk" -> painterResource(R.drawable.walk_new)
        "Ski" -> painterResource(R.drawable.ski_new)
        "Canoe" -> painterResource(R.drawable.canoe_new)
        "Swim" -> painterResource(R.drawable.swim_new)
        "Run" -> painterResource(R.drawable.run_new)
        else -> painterResource(R.drawable.mountains)
    }
}

fun getText(activity: Activity, index: Int): String {
    val exclamations = listOf(
        "Woohoo!", "Nice!", "Way to go!",
        "Well done!", "Yay!", "Very cool!",
        "Sweet!", "Awesome!", "Yippee!",
        "Bravo!", "Right on!", "Very fun!"
    )
    val date = formatDate(activity.startDate!!)
    val distance = String.format(Locale.ENGLISH, "%.2f", activity.distance!! / 1000.0)
    val time = formatSeconds(activity.elapsedTime!!)
    val elevation = String.format(Locale.ENGLISH, "%.2f", activity.elevationGain!! / 1000.0)

    // TODO: format- make it look better
    return "${exclamations[index % 12]} " +
            "on ${date.month.name.lowercase().replaceFirstChar { it.uppercase() }} " +
            "${date.dayOfMonth}, ${date.year} " +
            "you went on a $distance km ${activity.type!!.lowercase()}. " +
            "Sounds super fun and cool! \n" +
            "Your total time was ${time.first} hours and ${time.second} minutes \n" +
            "Your total elevation gain was $elevation \n" +
            "Way to go, you are a true athlete"

}