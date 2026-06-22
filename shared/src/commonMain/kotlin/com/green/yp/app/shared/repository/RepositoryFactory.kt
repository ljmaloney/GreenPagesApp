package com.green.yp.app.shared.repository

import com.green.yp.app.config.PlatformConfig
import com.green.yp.app.shared.api.ClassifiedApi
import com.green.yp.app.shared.network.HttpClientFactory
import de.jensklingenberg.ktorfit.Ktorfit

object RepositoryFactory {

    private val httpClient =
        HttpClientFactory.create(PlatformConfig.baseUrl)

    private val ktorfit =
        Ktorfit.Builder()
            .baseUrl(PlatformConfig.baseUrl)
            .httpClient(httpClient)
            .build()

    fun createClassifiedRepository(): ClassifiedRepository {

        return ClassifiedRepositoryImpl(
            ktorfit.create<ClassifiedApi>()
        )
    }
}