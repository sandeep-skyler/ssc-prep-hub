// Location: app/src/main/java/com/sscprephub/app/domain/model/Models.kt
package com.sscprephub.app.domain.model

import com.sscprephub.app.data.local.entity.SubjectEntity

data class SubjectWithStats(
    val subject: SubjectEntity,
    val totalTopics: Int,
    val completedTopics: Int,
    val progressPercentage: Int
)

data class DashboardStats(
    val totalTopics: Int = 0,
    val completedTopics: Int = 0,
    val totalPYTPapers: Int = 0,
    val totalPlaylists: Int = 0,
    val overallProgress: Int = 0
)
