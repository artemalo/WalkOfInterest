package sfedu.ictis.walkOfInterest.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import android.util.Log
import kotlinx.coroutines.ExperimentalCoroutinesApi

class SessionManager {
    private val _logoutEvent = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)
    val logoutEvent = _logoutEvent.asSharedFlow()

    fun triggerLogout() {
        Log.d("SessionManager", "triggerLogout called", Throwable())
        _logoutEvent.tryEmit(Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun resetLogout() {
        _logoutEvent.resetReplayCache()
    }
}