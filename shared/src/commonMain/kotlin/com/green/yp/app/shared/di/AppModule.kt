package com.green.yp.app.shared.di

import com.green.yp.app.config.PlatformConfig
import com.green.yp.app.shared.network.HttpClientFactory
import com.green.yp.app.shared.api.ClassifiedApi
import com.green.yp.app.shared.api.ReferenceApi
import com.green.yp.app.shared.api.SearchApi
import com.green.yp.app.shared.repository.*
import com.green.yp.app.shared.viewmodel.ClassifiedViewModel
import com.green.yp.app.shared.viewmodel.ReferenceViewModel
import com.green.yp.app.shared.viewmodel.SearchViewModel
import de.jensklingenberg.ktorfit.Ktorfit
import org.koin.dsl.module
import io.ktor.client.*

val appModule = module {

    single {
        HttpClientFactory.create(PlatformConfig.baseUrl)
    }

    single {
        Ktorfit.Builder()
            .baseUrl(PlatformConfig.baseUrl)
            .httpClient(get<HttpClient>())
            .build()
    }

    single<ClassifiedApi> {
        get<Ktorfit>().create()
    }

    single<SearchApi> {
        get<Ktorfit>().create()
    }

    single<ReferenceApi> {
        get<Ktorfit>().create()
    }

    single<ClassifiedRepository> {
        ClassifiedRepositoryImpl(get<ClassifiedApi>())
    }

    single<SearchRepository> {
        SearchRepositoryImpl(get<SearchApi>())
    }

    single<ReferenceRepository> {
        ReferenceRepositoryImpl(get<ReferenceApi>())
    }

    factory {
        ClassifiedViewModel(get<ClassifiedRepository>())
    }

    factory {
        SearchViewModel(get<SearchRepository>())
    }

    factory {
        ReferenceViewModel(get<ReferenceRepository>())
    }
}