package com.jz.hiker.main

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jz.hiker.common.AuthenticationState
import com.jz.hiker.common.DataFetchedState
import com.jz.hiker.strava.Activity
import com.jz.hiker.strava.Athlete
import com.jz.hiker.strava.StravaConnectionManager
import com.jz.hiker.strava.TokenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {
    var authState by mutableStateOf(AuthenticationState.Unauthorized)
    var dataFetched by mutableStateOf(DataFetchedState.Unfetched)
    var boxExpanded: Long? by mutableStateOf(null)
    // TODO: Store access token persistently
    private var currentAccessToken: TokenResponse? = null
    var athleteData: Athlete? = null
    var athleteActivities: List<Activity>? = null
    var toastMessage: String? by mutableStateOf(null)

    private var connectionManager: StravaConnectionManager? = null

    fun initConnectionManager(context: Context) {
        this.connectionManager = StravaConnectionManager(context)
    }

    fun clearToastMessage() {
        toastMessage = null
    }

    fun loginStravaAccount() {
        authState = AuthenticationState.WaitingForAuth
        connectionManager?.startOAuth()
    }

    fun logoutStravaAccount(e: String? = null) {
        e?.let {
            Log.e(javaClass.simpleName, it)
            toastMessage = e
        }
        viewModelScope.launch(Dispatchers.IO) {
            currentAccessToken?.let {
                connectionManager?.deAuth(it.accessToken)
            }
            currentAccessToken = null
            athleteData = null
            athleteActivities = null
            boxExpanded = null
            dataFetched = DataFetchedState.Unfetched
            authState = AuthenticationState.Unauthorized
        }
    }

    fun handleOAuthRedirect(intent: Intent?) {
        // TODO: Handle incorrect scope case
        intent?.data?.let { uri ->
            val code = uri.getQueryParameter(StravaConnectionManager.QUERY_PARAM_CODE)
            val scope = uri.getQueryParameter(StravaConnectionManager.QUERY_PARAM_SCOPE)
            // ignore if intent does not contain code or scope
            code ?: return
            scope ?: return
            viewModelScope.launch(Dispatchers.IO) {
                authState = AuthenticationState.WaitingForAuth
                val token = connectionManager?.exchangeToken(code)
                // if no token is received revert to login screen
                token?.let {
                    currentAccessToken = it
                    authState = AuthenticationState.Authorized
                } ?: logoutStravaAccount("handleOAuthRedirect: no token")
            }
        }
    }

    fun getUserData() {
        // TODO: Handle all error / null cases
        viewModelScope.launch(Dispatchers.IO) {
            if (currentAccessToken == null || connectionManager == null) {
                logoutStravaAccount("getUserData: no token or manager")
                return@launch
            }
            val athlete: Athlete? = connectionManager!!.getAuthenticatedAthlete(currentAccessToken!!.accessToken)
            if (athlete == null) {
                logoutStravaAccount("getUserData: no Athlete data")
                return@launch
            }
            athleteData = athlete
            val activities = connectionManager!!.getRecentActivities(currentAccessToken!!.accessToken)
            activities?.let {
                athleteActivities = it
                dataFetched = DataFetchedState.Fetched
            }
        }
    }

    fun activityChosen(id: Long) {
        if (boxExpanded == id) boxExpanded = null
        else boxExpanded = id
    }

}