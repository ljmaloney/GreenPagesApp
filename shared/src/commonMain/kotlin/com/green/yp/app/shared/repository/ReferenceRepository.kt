package com.green.yp.app.shared.repository

import com.green.yp.app.shared.dto.reference.LineOfBusiness
import kotlinx.coroutines.flow.StateFlow

interface ReferenceRepository {
    val linesOfBusiness: StateFlow<List<LineOfBusiness>>
    val errorMessage: StateFlow<String?>
    suspend fun getLinesOfBusiness(): Result<List<LineOfBusiness>>
}
