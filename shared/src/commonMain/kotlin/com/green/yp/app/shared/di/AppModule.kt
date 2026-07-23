package com.green.yp.app.shared.di

import com.green.yp.app.config.PlatformConfig
import com.green.yp.app.payment.SquarePaymentProcessor
import com.green.yp.app.shared.api.ClassifiedApi
import com.green.yp.app.shared.api.ClassifiedReferenceApi
import com.green.yp.app.shared.api.EmailContactApi
import com.green.yp.app.shared.api.ReferenceApi
import com.green.yp.app.shared.api.SearchApi
import com.green.yp.app.shared.network.HttpClientFactory
import com.green.yp.app.shared.repository.ClassifiedReferenceRepository
import com.green.yp.app.shared.repository.ClassifiedReferenceRepositoryImpl
import com.green.yp.app.shared.repository.ClassifiedRepository
import com.green.yp.app.shared.repository.ClassifiedRepositoryImpl
import com.green.yp.app.shared.repository.EmailContactRepository
import com.green.yp.app.shared.repository.EmailContactRepositoryImpl
import com.green.yp.app.shared.repository.ReferenceRepository
import com.green.yp.app.shared.repository.ReferenceRepositoryImpl
import com.green.yp.app.shared.repository.SearchRepository
import com.green.yp.app.shared.repository.SearchRepositoryImpl
import com.green.yp.app.shared.viewmodel.ClassifiedReferenceViewModel
import com.green.yp.app.shared.viewmodel.ClassifiedViewModel
import com.green.yp.app.shared.viewmodel.EmailContactViewModel
import com.green.yp.app.shared.viewmodel.ReferenceViewModel
import com.green.yp.app.shared.viewmodel.SearchViewModel
import com.green.yp.app.wizard.ClassifiedWizardViewModel
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import org.koin.dsl.module

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

    single<ClassifiedReferenceApi> {
        get<Ktorfit>().create()
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

    single<EmailContactApi> {
        get<Ktorfit>().create()
    }

    single<ClassifiedReferenceRepository> {
        ClassifiedReferenceRepositoryImpl(get<ClassifiedReferenceApi>())
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

    single<EmailContactRepository> {
        EmailContactRepositoryImpl(get<EmailContactApi>())
    }

    factory {
        ClassifiedReferenceViewModel(get<ClassifiedReferenceRepository>())
    }

    factory {
        ClassifiedViewModel(get<ClassifiedRepository>())
    }

    factory {
        EmailContactViewModel(get<EmailContactRepository>())
    }

    factory {
        SearchViewModel(get<SearchRepository>())
    }

    factory {
        ReferenceViewModel(get<ReferenceRepository>())
    }

    factory {
        ClassifiedWizardViewModel(
            get<ClassifiedRepository>(),
            get<EmailContactViewModel>(),
            get<SquarePaymentProcessor>()
        )
    }
}
