package com.erp.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import com.erp.models.*

class ERPClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private var authToken: String? = null
    private val baseUrl = "http://localhost:8080/api"

    fun setAuthToken(token: String) {
        authToken = token
    }

    private fun HttpRequestBuilder.authorized() {
        authToken?.let { token ->
            header("Authorization", "Bearer $token")
        }
    }

    suspend fun login(username: String, password: String): AuthResponse {
        return client.post("$baseUrl/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "username" to username,
                "password" to password
            ))
        }.body()
    }

    suspend fun getProjects(): List<Project> {
        return client.get("$baseUrl/projects") {
            authorized()
        }.body()
    }

    suspend fun getProject(id: Int): Project {
        return client.get("$baseUrl/projects/$id") {
            authorized()
        }.body()
    }

    suspend fun createProject(project: Project): Project {
        return client.post("$baseUrl/projects") {
            authorized()
            contentType(ContentType.Application.Json)
            setBody(project)
        }.body()
    }

    suspend fun getWorkOrders(projectId: Int): List<WorkOrder> {
        return client.get("$baseUrl/projects/$projectId/work-orders") {
            authorized()
        }.body()
    }

    suspend fun createWorkOrder(workOrder: WorkOrder): WorkOrder {
        return client.post("$baseUrl/work-orders") {
            authorized()
            contentType(ContentType.Application.Json)
            setBody(workOrder)
        }.body()
    }

    suspend fun getProjectMetrics(projectId: Int): ProjectMetrics {
        return client.get("$baseUrl/analytics/project-metrics/$projectId") {
            authorized()
        }.body()
    }

    suspend fun logTime(timeEntry: TimeEntry): TimeEntry {
        return client.post("$baseUrl/time-entries") {
            authorized()
            contentType(ContentType.Application.Json)
            setBody(timeEntry)
        }.body()
    }
}