package sfedu.ictis.walkOfInterest.di

import androidx.room.Room
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sfedu.ictis.walkOfInterest.BuildConfig
import sfedu.ictis.walkOfInterest.data.api.AuthApi
import sfedu.ictis.walkOfInterest.data.api.RouteApi
import sfedu.ictis.walkOfInterest.data.local.AppDatabase
import sfedu.ictis.walkOfInterest.data.local.AuthInterceptor
import sfedu.ictis.walkOfInterest.data.local.TokenAuthenticator
import sfedu.ictis.walkOfInterest.data.local.TokenStorage
import sfedu.ictis.walkOfInterest.data.local.TokenStorageImpl
import sfedu.ictis.walkOfInterest.data.repository.AuthRepositoryImpl
import sfedu.ictis.walkOfInterest.data.repository.MapSettingRepositoryImpl
import sfedu.ictis.walkOfInterest.data.repository.RouteRepositoryImpl
import sfedu.ictis.walkOfInterest.data.repository.TripRepositoryImpl
import sfedu.ictis.walkOfInterest.domain.repository.AuthRepository
import sfedu.ictis.walkOfInterest.domain.repository.MapSettingRepository
import sfedu.ictis.walkOfInterest.domain.repository.RouteRepository
import sfedu.ictis.walkOfInterest.domain.repository.TripRepository
import sfedu.ictis.walkOfInterest.domain.usecase.CalculateWalkUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.DeleteTripByIdUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetRoutesUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetBaseRouteUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetCurrentTripUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetMapCenterUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetTripByIdUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetTripsUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.LoginUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.RegisterUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.SaveTripUseCase
import sfedu.ictis.walkOfInterest.presentation.auth.AuthViewModel
import sfedu.ictis.walkOfInterest.presentation.categories.CategoriesViewModel
import sfedu.ictis.walkOfInterest.presentation.category.CategoryViewModel
import sfedu.ictis.walkOfInterest.presentation.details.TripDetailsViewModel
import sfedu.ictis.walkOfInterest.presentation.generate.GenerateViewModel
import sfedu.ictis.walkOfInterest.presentation.main.MainFeedViewModel
import sfedu.ictis.walkOfInterest.presentation.routes.RoutesViewModel
import sfedu.ictis.walkOfInterest.utils.SessionManager

val networkModule = module {
    single { SessionManager() }

    single<OkHttpClient>(named("protected_client")) {
        OkHttpClient.Builder()
            .authenticator(
                TokenAuthenticator(
                    get(),
                    get(named("auth_api")
                ),
                get())
            ) // Обновляем токен, если 401
            .addInterceptor(AuthInterceptor(get()))
            .build()
    }

    single(named("auth_client")) {
        OkHttpClient.Builder().build()
    }

    single<AuthApi>(named("auth_api")) {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(get(named("auth_client")))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    single<RouteApi> {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(get(named("protected_client")))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RouteApi::class.java)
    }
}

val appModule = module {
    // Data Layer: Network & Repository
    single<TokenStorage> { TokenStorageImpl(androidContext()) }
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "walk_of_interest_db"
        ).build()
    }
    single { get<AppDatabase>().tripDao() }

    // single
    single<AuthRepository> { AuthRepositoryImpl(get(named("auth_api")), get()) }
    single<MapSettingRepository> { MapSettingRepositoryImpl() }
    single<TripRepository> { TripRepositoryImpl(get()) }
    single<RouteRepository> { RouteRepositoryImpl(get()) }

    // Domain Layer: Use Cases
    factory { RegisterUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { GetBaseRouteUseCase(get()) }
    factory { CalculateWalkUseCase(get()) }
    factory { GetRoutesUseCase(get()) }
    factory { GetTripsUseCase(get()) }
    factory { GetCurrentTripUseCase(get()) }
    factory { GetMapCenterUseCase(get()) }
    factory { SaveTripUseCase(get()) }
    factory { GetTripByIdUseCase(get()) }
    factory { DeleteTripByIdUseCase(get()) }

    // Presentation Layer: ViewModels
    viewModel { AuthViewModel(get(), get()) }
    viewModel { TripDetailsViewModel(get(), get()) }
    viewModel { MainFeedViewModel(get()) }
    viewModel { GenerateViewModel(get(), get(), get()) }
    viewModel { CategoriesViewModel(get())}
    viewModel { CategoryViewModel() }
    viewModel { RoutesViewModel(get(), get(), get()) }
}