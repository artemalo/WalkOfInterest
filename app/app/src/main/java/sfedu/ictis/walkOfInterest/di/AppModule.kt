package sfedu.ictis.walkOfInterest.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import sfedu.ictis.walkOfInterest.data.repository.RouteRepositoryImpl
import sfedu.ictis.walkOfInterest.domain.repository.RouteRepository
import sfedu.ictis.walkOfInterest.domain.usecase.CalculateWalkUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetRoutesUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetBaseRouteUseCase
import sfedu.ictis.walkOfInterest.infrastructure.network.NetworkModule
import sfedu.ictis.walkOfInterest.presentation.categories.CategoriesViewModel
import sfedu.ictis.walkOfInterest.presentation.generate.GenerateViewModel
import sfedu.ictis.walkOfInterest.presentation.routes.RoutesViewModel

val appModule = module {

    // 1. Data Layer: Network & Repository
    single { NetworkModule.routeApi } // Используем твой существующий API

    // single — создает объект один раз (синглтон)
    single<RouteRepository> { RouteRepositoryImpl(get()) }

    // 2. Domain Layer: Use Cases
    factory { GetBaseRouteUseCase(get()) } // factory — создает новый экземпляр при каждом запросе
    factory { CalculateWalkUseCase(get()) }
    factory { GetRoutesUseCase(get()) }

    // 3. Presentation Layer: ViewModels
    viewModel { GenerateViewModel(get(), get()) }
    viewModel { CategoriesViewModel(get()) } // если там будут UseCase, добавь get()
    viewModel { RoutesViewModel(get(), get()) }
}