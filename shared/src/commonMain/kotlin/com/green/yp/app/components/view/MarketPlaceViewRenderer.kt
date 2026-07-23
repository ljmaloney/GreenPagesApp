package com.green.yp.app.components.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.green.yp.app.shared.dto.classified.ImageGallery
import com.green.yp.app.shared.dto.search.SearchResponseDTO

fun interface MarketPlaceViewRenderer {
    @Composable
    fun renderView(
        result: SearchResponseDTO,
        modifier: Modifier?,
        onClick: () -> Unit
    )

    fun <T> getImageGallery(result: SearchResponseDTO): List<T> {
        @Suppress("UNCHECKED_CAST")
        return emptyList<ImageGallery>() as List<T>
    }
}