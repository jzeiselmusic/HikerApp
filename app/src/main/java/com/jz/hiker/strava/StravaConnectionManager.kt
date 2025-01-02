package com.jz.hiker.strava

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

class StravaConnectionManager(private val context: Context) {
    companion object {
        // TODO: move to build config for security
        private const val CLIENT_ID = "142295"
        private const val CLIENT_SECRET = "57a0ad30a6a4cb403ec687520295500ac18360be"
        private const val REDIRECT_URI ="http://localhost:7001/hiker.com/oauth"
        private const val BASE_URL = "https://www.strava.com/api/v3/"
        private const val AUTH_URL = "https://www.strava.com/oauth/mobile/authorize"

        const val QUERY_PARAM_CODE = "code"
        const val QUERY_PARAM_SCOPE = "scope"
        const val QUERY_PARAM_FORCE = "force"
    }

    private val api: StravaApi

    init {
        val client = OkHttpClient.Builder().build()
        api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(StravaApi::class.java)
    }

    fun startOAuth() {
        val authUrl = Uri.parse(AUTH_URL)
            .buildUpon()
            .appendQueryParameter("client_id", CLIENT_ID)
            .appendQueryParameter("response_type", QUERY_PARAM_CODE)
            .appendQueryParameter("redirect_uri", REDIRECT_URI)
            .appendQueryParameter("approval_prompt", QUERY_PARAM_FORCE)
            // requires read and read_all permissions
            .appendQueryParameter(
                QUERY_PARAM_SCOPE,
                "activity:read,activity:read_all"
            )
            .build()

        val intent = Intent(Intent.ACTION_VIEW, authUrl)
        context.startActivity(intent)
        Log.d(javaClass.simpleName, "starting Oauth activity")
    }

    suspend fun deAuth(accessToken: String) {
        try {
            api.deauthorize(accessToken = accessToken)
            Log.d(javaClass.simpleName, "Deauthorization success")
        } catch (e: HttpException) {
            Log.e(javaClass.simpleName,"[deAuth] ${e.code()} : ${e.cause} : ${e.message}")
        }
    }

    suspend fun exchangeToken(authCode: String): TokenResponse? {
        var response: TokenResponse?
        try {
            response = api.exchangeToken(
                clientId = CLIENT_ID,
                clientSecret = CLIENT_SECRET,
                code = authCode,
                grantType = "authorization_code"
            )
            Log.d(javaClass.simpleName, "exchangeToken success")
        } catch (e: HttpException) {
            Log.e(javaClass.simpleName,
                "[exchangeToken] ${e.code()} : ${e.cause} : ${e.message}")
            response = null
        }
        return response
    }

    suspend fun refreshToken(refreshToken: String): TokenResponse? {
        var response: TokenResponse?
        try {
            response = api.refreshToken(
                clientId = CLIENT_ID,
                clientSecret = CLIENT_SECRET,
                refreshToken = refreshToken,
                grantType = "refresh_token"
            )
            Log.d(javaClass.simpleName, "refreshToken success")
        } catch (e: HttpException) {
            response = null
            Log.e(javaClass.simpleName,
                "[refreshToken] ${e.code()} : ${e.cause} : ${e.message}")
        }
        return response
    }

    suspend fun getAuthenticatedAthlete(accessToken: String): Athlete? {
        var athlete: Athlete?
        try {
            athlete = api.getAuthenticatedAthlete("Bearer $accessToken")
            Log.d(javaClass.simpleName, "getAthlete success")
        } catch (e: HttpException) {
            athlete = null
            Log.e(javaClass.simpleName,
                "[getAthlete] ${e.code()} : ${e.cause} : ${e.message}")
        }
        return athlete
    }

    suspend fun getRecentActivities(accessToken: String): List<Activity>? {
        var activities: List<Activity>?
        try {
            activities = api.getRecentActivities(
                token = "Bearer $accessToken",
                page = 1,
                perPage = 10
            )
            // get maximum last 10 activities
            Log.d(javaClass.simpleName, "getRecentActivities success")
            Log.d(javaClass.simpleName, "activities: $activities")
        } catch (e: HttpException) {
            activities = null
            Log.e(javaClass.simpleName,
                "[getRecentActivities] ${e.code()} : ${e.cause} : ${e.message}")
        }
        return activities
    }


}

// REST API structure for Strava calls
// from https://developers.strava.com/docs/reference/#api-Activities
//
// Retrofit suspend funcs are automatically called on IO thread not MAIN
interface StravaApi {
    @POST("oauth/token")
    @FormUrlEncoded
    suspend fun exchangeToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("grant_type") grantType: String
    ): TokenResponse

    // from https://developers.strava.com/docs/authentication/#deauthorization
    @POST("oauth/deauthorize")
    @FormUrlEncoded
    suspend fun deauthorize(
        @Field("access_token") accessToken: String
    )

    @POST("oauth/token")
    @FormUrlEncoded
    suspend fun refreshToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String
    ): TokenResponse

    @GET("athlete")
    suspend fun getAuthenticatedAthlete(
        @Header("Authorization") token: String
    ): Athlete

    // Get (PER_PAGE*PAGE) activities
    @GET("athlete/activities")
    suspend fun getRecentActivities(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<Activity>
}

// Data classes representing data types from Strava API Documentation
// https://developers.strava.com/docs/reference/#api-models-ActivityStats
data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("expires_at") val expiresAt: Long,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("token_type") val tokenType: String
)

data class ActivityStats(
    @SerializedName("biggest_ride_distance") val biggestRideDistance: Double?,
    @SerializedName("biggest_climb_elevation_gain") val biggestClimbElevationGain: Double?,
    @SerializedName("recent_ride_totals") val recentRideTotals: ActivityTotal?,
    @SerializedName("recent_run_totals") val recentRunTotals: ActivityTotal?,
    @SerializedName("recent_swim_totals") val recentSwimTotals: ActivityTotal?,
    @SerializedName("ytd_ride_totals") val ytdRideTotals: ActivityTotal?,
    @SerializedName("ytd_run_totals") val ytdRunTotals: ActivityTotal?,
    @SerializedName("ytd_swim_totals") val ytdSwimTotals: ActivityTotal?,
    @SerializedName("all_ride_totals") val allRideTotals: ActivityTotal?,
    @SerializedName("all_run_totals") val allRunTotals: ActivityTotal?,
    @SerializedName("all_swim_totals") val allSwimTotals: ActivityTotal?
)

data class ActivityTotal(
    @SerializedName("count") val count: Int?,
    @SerializedName("distance") val distance: Double?,
    @SerializedName("moving_time") val movingTime: Int?,
    @SerializedName("elapsed_time") val elapsedTime: Int?,
    @SerializedName("elevation_gain") val elevationGain: Double?,
    @SerializedName("achievement_count") val achievementCount: Int?
)

data class Athlete(
    @SerializedName("id") val id: Long?,
    @SerializedName("firstname") val firstName: String?,
    @SerializedName("lastname") val lastName: String?,
    @SerializedName("profile") val profileImage: String?
)

data class Activity(
    @SerializedName("id") val id: Long?,
    @SerializedName("name") val name: String?,
    @SerializedName("distance") val distance: Double?,
    @SerializedName("moving_time") val movingTime: Int?,
    @SerializedName("elapsed_time") val elapsedTime: Int?,
    @SerializedName("type") val type: String?,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("total_elevation_gain") val elevationGain: Double?
)