package com.erp.components

import react.*
import react.dom.html.ReactHTML as html
import kotlinx.coroutines.launch
import com.erp.models.*
import com.erp.api.ERPClient
import csstype.*
import emotion.react.css
import kotlin.js.Date

external interface DashboardProps : Props {
    var client: ERPClient
}

val Dashboard = FC<DashboardProps> { props ->
    var projects by useState<List<Project>>(emptyList())
    var selectedProject by useState<Project?>(null)
    var metrics by useState<ProjectMetrics?>(null)
    var loading by useState(false)
    var error by useState<String?>(null)

    useEffectOnce {
        loadProjects()
    }

    fun loadProjects() {
        loading = true
        props.client.mainScope.launch {
            try {
                projects = props.client.getProjects()
            } catch (e: Exception) {
                error = "Failed to load projects: ${e.message}"
            } finally {
                loading = false
            }
        }
    }

    fun loadProjectMetrics(projectId: Int) {
        props.client.mainScope.launch {
            try {
                metrics = props.client.getProjectMetrics(projectId)
            } catch (e: Exception) {
                error = "Failed to load metrics: ${e.message}"
            }
        }
    }

    html.div {
        css {
            padding = 20.px
        }

        html.h1 {
            +"ERP Dashboard"
            css {
                fontSize = 24.px
                fontWeight = FontWeight.bold
                marginBottom = 16.px
            }
        }

        error?.let {
            Alert {
                type = AlertType.ERROR
                message = it
                onClose = { error = null }
            }
        }

        html.div {
            css {
                display = Display.grid
                gridTemplateColumns = array(1.fr, 1.fr)
                gap = 16.px
            }

            // Projects List
            Card {
                title = "Projects"

                html.div {
                    projects.forEach { project ->
                        ProjectCard {
                            this.project = project
                            selected = project.id == selectedProject?.id
                            onClick = {
                                selectedProject = project
                                loadProjectMetrics(project.id)
                            }
                        }
                    }
                }
            }

            // Project Details
            selectedProject?.let { project ->
                Card {
                    title = "Project Details: ${project.name}"

                    html.div {
                        css {
                            display = Display.flex
                            flexDirection = FlexDirection.column
                            gap = 8.px
                        }

                        DetailRow {
                            label = "Status"
                            value = project.status.toString()
                        }

                        DetailRow {
                            label = "Budget"
                            value = "$${project.budget}"
                        }

                        DetailRow {
                            label = "Actual Cost"
                            value = "$${project.actualCost}"
                        }

                        metrics?.let { metrics ->
                            html.div {
                                css {
                                    marginTop = 16.px
                                }

                                html.h3 {
                                    +"Project Metrics"
                                    css {
                                        fontSize = 18.px
                                        fontWeight = FontWeight.bold
                                        marginBottom = 8.px
                                    }
                                }

                                MetricsChart {
                                    this.metrics = metrics
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

external interface ProjectCardProps : Props {
    var project: Project
    var selected: Boolean
    var onClick: () -> Unit
}

val ProjectCard = FC<ProjectCardProps> { props ->
    html.div {
        css {
            padding = 16.px
            borderRadius = 4.px
            border = Border(1.px, LineStyle.solid, if (props.selected) Color("#1a73e8") else Color("#e0e0e0"))
            cursor = Cursor.pointer
            backgroundColor = if (props.selected) Color("#f8f9fa") else Color.white
            hover {
                backgroundColor = Color("#f8f9fa")
            }
        }
        onClick = { props.onClick() }

        html.h3 {
            +props.project.name
            css {
                fontSize = 16.px
                fontWeight = FontWeight.bold
                marginBottom = 8.px
            }
        }

        html.p {
            +props.project.description
            css {
                fontSize = 14.px
                color = Color("#666")
            }
        }

        html.div {
            css {
                marginTop = 8.px
                display = Display.flex
                gap = 16.px
            }

            StatusBadge {
                status = props.project.status
            }

            html.span {
                +"Due: ${props.project.endDate}"
                css {
                    fontSize = 12.px
                    color = Color("#666")
                }
            }
        }
    }
}

external interface MetricsChartProps : Props {
    var metrics: ProjectMetrics
}

val MetricsChart = FC<MetricsChartProps> { props ->
    // Using a custom chart implementation with kotlinx.html
    html.div {
        css {
            width = 100.pct
            height = 200.px
            position = Position.relative
        }

        html.div {
            css {
                position = Position.absolute
                bottom = 0.px
                left = 0.px
                width = 100.pct
                height = (props.metrics.progressPercentage).pct
                backgroundColor = Color("#1a73e8")
                opacity = number(0.7)
                transition = "height 0.3s ease-in-out"
            }
        }

        html.div {
            css {
                position = Position.absolute
                top = 50.pct
                left = 50.pct
                transform = translate((-50).pct, (-50).pct)
                fontSize = 24.px
                fontWeight = FontWeight.bold
                color = Color("#1a73e8")
            }
            +"${props.metrics.progressPercentage}%"
        }
    }
}