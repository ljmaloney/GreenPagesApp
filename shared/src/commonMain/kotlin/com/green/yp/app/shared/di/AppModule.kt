package com.green.yp.app.shared.di

import com.green.yp.app.config.PlatformConfig
import com.green.yp.app.shared.network.HttpClientFactory
import com.green.yp.app.shared.api.ClassifiedApi
import com.green.yp.app.shared.repository.ClassifiedRepository
import com.green.yp.app.shared.repository.ClassifiedRepositoryImpl
import com.green.yp.app.shared.viewmodel.ClassifiedViewModel
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

    single<ClassifiedRepository> {
        ClassifiedRepositoryImpl(get<ClassifiedApi>())
    }

    factory {
        ClassifiedViewModel(get<ClassifiedRepository>())
    }
}