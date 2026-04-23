package sfedu.ictis.walkOfInterest.data.local

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import sfedu.ictis.walkOfInterest.data.api.AuthApi
import sfedu.ictis.walkOfInterest.data.model.RefreshRequest
import sfedu.ictis.walkOfInterest.utils.SessionManager

class TokenAuthenticator(
    private val tokenStorage: TokenStorage,
    private val authApi: AuthApi,
    private val sessionManager: SessionManager
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = tokenStorage.getRefreshToken() ?: return null

        val refreshResponse = runBlocking {
            try {
                authApi.refresh(RefreshRequest(refreshToken))
            } catch (_: Exception) {
                null
            }
        }

        return if (refreshResponse != null) {
            tokenStorage.saveTokens(refreshResponse.accessToken, refreshResponse.refreshToken)

            response.request.newBuilder()
                .header("Authorization", "Bearer ${refreshResponse.accessToken}")
                .build()
        } else {
            tokenStorage.clear()
            sessionManager.triggerLogout()
            null
        }
    }
}