package sfedu.ictis.walkOfInterest.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import sfedu.ictis.walkOfInterest.data.repository.MapRepositoryImpl
import sfedu.ictis.walkOfInterest.data.repository.RouteRepositoryImpl
import sfedu.ictis.walkOfInterest.domain.repository.MapRepository
import sfedu.ictis.walkOfInterest.domain.repository.RouteRepository
import sfedu.ictis.walkOfInterest.domain.usecase.CalculateWalkUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetRoutesUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetBaseRouteUseCase
import sfedu.ictis.walkOfInterest.infrastructure.network.NetworkModule
import sfedu.ictis.walkOfInterest.presentation.categories.CategoriesViewModel
import sfedu.ictis.walkOfInterest.presentation.generate.GenerateViewModel
import sfedu.ictis.walkOfInterest.presentation.routes.RoutesViewModel

val appModule = module {

    // Data Layer: Network & Repository
    single { NetworkModule.routeApi }

    // single
    single<MapRepository> { MapRepositoryImpl() }
    single<RouteRepository> { RouteRepositoryImpl(get()) }

    // Domain Layer: Use Cases
    factory { GetBaseRouteUseCase(get()) }
    factory { CalculateWalkUseCase(get()) }
    factory { GetRoutesUseCase(get()) }

    // Presentation Layer: ViewModels
    viewModel { GenerateViewModel(get(), get(), get()) }
    viewModel { CategoriesViewModel(get()) }
    viewModel { RoutesViewModel(get(), get(), get()) }
}