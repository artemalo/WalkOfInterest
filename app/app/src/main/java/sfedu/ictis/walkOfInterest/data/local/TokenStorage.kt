package sfedu.ictis.walkOfInterest.data.local

interface TokenStorage {
    fun saveAccessToken(token: String)
    fun getAccessToken(): String?
    fun saveRefreshToken(token: String)
    fun getRefreshToken(): String?

    fun saveTokens(accessToken: String, refreshToken: String)
    fun clear()
}