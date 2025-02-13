package com.erp.models

import kotlinx.serialization.Serializable
import kotlin.js.Date

@Serializable
data class User(
    val id: Int,
    val username: String,
    val email: String,
    val role: UserRole
)

@Serializable
enum class UserRole {
    ADMIN, MANAGER, WORKER
}

@Serializable
data class Project(
    val id: Int,
    val name: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val budget: Double,
    val actualCost: Double,
    val status: ProjectStatus
)

@Serializable
enum class ProjectStatus {
    PLANNED, IN_PROGRESS, COMPLETED, ON_HOLD
}

@Serializable
data class WorkOrder(
    val id: Int,
    val projectId: Int,
    val bomId: Int,
    val status: WorkOrderStatus,
    val quantity: Int,
    val startDate: String,
    val endDate: String
)

@Serializable
enum class WorkOrderStatus {
    PLANNED, IN_PROGRESS, COMPLETED, CANCELLED
}

@Serializable
data class ProjectMetrics(
    val progressPercentage: Double,
    val costVariance: Double,
    val scheduleVariance: Int,
    val resourceUtilization: Double
)

@Serializable
data class TimeEntry(
    val id: Int,
    val userId: Int,
    val projectId: Int,
    val workOrderId: Int?,
    val startTime: String,
    val endTime: String,
    val description: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: User
)