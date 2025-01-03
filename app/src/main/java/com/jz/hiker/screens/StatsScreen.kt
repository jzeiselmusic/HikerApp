package com.jz.hiker.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jz.hiker.common.BackButton
import com.jz.hiker.common.DataFetchedState
import com.jz.hiker.common.HikerConfig
import com.jz.hiker.common.ProgressSpinner
import com.jz.hiker.common.RoundedBox
import com.jz.hiker.common.isPortrait
import com.jz.hiker.main.AppViewModel
import com.jz.hiker.strava.Activity
import com.jz.hiker.ui.theme.Typography

@Composable
fun StatsScreen(viewModel: AppViewModel) {
    LaunchedEffect(Unit) {
        viewModel.getUserData()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        when(viewModel.dataFetched) {
            DataFetchedState.Fetched -> StatsContent(viewModel)
            else -> ProgressSpinner(modifier = Modifier.align(Alignment.Center)) { }
        }
    }
}

@Composable
fun StatsContent(viewModel: AppViewModel) {
    val isPortrait = isPortrait()

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.size(
            if (isPortrait)
                HikerConfig.initialOffsetPortrait
            else
                HikerConfig.initialOffsetLandscape)
        )

        TopBar(viewModel)

        ActivityList(
            activities = viewModel.athleteActivities ?: emptyList(),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal =
                    if (isPortrait)
                        HikerConfig.horizontalPaddingPortrait
                    else
                        HikerConfig.horizontalPaddingLandscape
                ),
            currentExpanded = viewModel.boxExpanded,
            onPress = viewModel::activityChosen
        )
    }
}

@Composable
fun TopBar(viewModel: AppViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackButton(modifier = Modifier.align(Alignment.CenterVertically)) {
            viewModel.logoutStravaAccount()
        }

        Spacer(modifier = Modifier.weight(0.75f))

        Text(
            style = Typography.headlineMedium,
            text = "Hello, ${viewModel.athleteData?.firstName ?: "Athlete"}",
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun ActivityList(
    activities: List<Activity>,
    modifier: Modifier = Modifier,
    currentExpanded: Long?,
    onPress: (Long) -> Unit
) {
    if (activities.isNotEmpty()) {
        LazyColumn(
            modifier = modifier,
        ) {
            items(activities.size) { index ->
                RoundedBox(
                    data = activities[index],
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    index,
                    currentExpanded = currentExpanded
                ) { onPress(activities[index].id!!) }
            }
        }
    }
    else {
        // empty list case
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                style = Typography.headlineSmall,
                text = "You have no recent activities!",
                textAlign = TextAlign.Center,
                color = Color.LightGray.copy(alpha = 0.5f),
            )
        }
    }
}