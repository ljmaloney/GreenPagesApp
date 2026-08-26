package com.green.yp.app

import androidx.compose.runtime.Composable

/**
 * Initializes the resource context for Compose Multiplatform Previews.
 * On Android, this calls PreviewContextConfigurationEffect().
 * On other platforms, it is a no-op.
 */
@Composable
expect fun PreviewContext()
