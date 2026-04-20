package sfedu.ictis.walkOfInterest.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import sfedu.ictis.walkOfInterest.data.local.AppDatabase
import sfedu.ictis.walkOfInterest.data.repository.MapSettingRepositoryImpl
import sfedu.ictis.walkOfInterest.data.repository.RouteRepositoryImpl
import sfedu.ictis.walkOfInterest.data.repository.TripRepositoryImpl
import sfedu.ictis.walkOfInterest.domain.repository.MapSettingRepository
import sfedu.ictis.walkOfInterest.domain.repository.RouteRepository
import sfedu.ictis.walkOfInterest.domain.repository.TripRepository
import sfedu.ictis.walkOfInterest.domain.usecase.CalculateWalkUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetRoutesUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetBaseRouteUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetCurrentTripUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetMapCenterUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetTripByIdUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetTripsUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.SaveTripUseCase
import sfedu.ictis.walkOfInterest.infrastructure.network.NetworkModule
import sfedu.ictis.walkOfInterest.presentation.categories.CategoriesViewModel
import sfedu.ictis.walkOfInterest.presentation.details.TripDetailsViewModel
import sfedu.ictis.walkOfInterest.presentation.generate.GenerateViewModel
import sfedu.ictis.walkOfInterest.presentation.main.MainFeedViewModel
import sfedu.ictis.walkOfInterest.presentation.routes.RoutesViewModel

val appModule = module {

    // Data Layer: Network & Repository
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "walk_of_interest_db"
        ).build()
    }

    single { get<AppDatabase>().tripDao() }
    single { NetworkModule.routeApi }

    // single
    single<MapSettingRepository> { MapSettingRepositoryImpl() }
    single<TripRepository> { TripRepositoryImpl(get()) }
    single<RouteRepository> { RouteRepositoryImpl(get()) }

    // Domain Layer: Use Cases
    factory { GetBaseRouteUseCase(get()) }
    factory { CalculateWalkUseCase(get()) }
    factory { GetRoutesUseCase(get()) }
    factory { GetTripsUseCase(get()) }
    factory { GetCurrentTripUseCase(get()) }
    factory { GetMapCenterUseCase(get()) }
    factory { SaveTripUseCase(get()) }
    factory { GetTripByIdUseCase(get()) }

    // Presentation Layer: ViewModels
    viewModel { TripDetailsViewModel(get()) }
    viewModel { MainFeedViewModel(get()) }
    viewModel { GenerateViewModel(get(), get(), get()) }
    viewModel { CategoriesViewModel(get())}
    viewModel { RoutesViewModel(get(), get(), get()) }
}