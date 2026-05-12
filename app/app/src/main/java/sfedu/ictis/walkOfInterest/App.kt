package sfedu.ictis.walkOfInterest

import android.app.Application
import coil.Coil
import coil.ImageLoader
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import sfedu.ictis.walkOfInterest.di.appModule
import sfedu.ictis.walkOfInterest.di.networkModule

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule, networkModule)
        }
        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .okHttpClient {
                    OkHttpClient.Builder()
                        .followRedirects(true)
                        .followSslRedirects(true)
                        .build()
                }
                .crossfade(true)
                .build()
        )
    }
}