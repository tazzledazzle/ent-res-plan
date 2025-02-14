package com.erp

import react.*
import react.dom.client.createRoot
import kotlinx.browser.document
import kotlinx.browser.window
import com.erp.api.ERPClient
import com.erp.components.*
import react.router.dom.*
import emotion.react.css
import csstype.*
import kotlinx.coroutines.*

fun main() {
    val container = document.createElement("div")
    document.body!!.appendChild(container)

    val client = ERPClient()

    createRoot(container).render(createElement(FC {
        // Global styles
        Style {
            css {
                "body" {
                    margin = 0.px
                    padding = 0.px
                    fontFamily = "Inter, system-ui, sans-serif"
                    backgroundColor = Color("#f5f5f5")
                }

                ".nav-link" {
                    display = Display.block
                    padding = Padding(12.px, 16.px)
                    color = Color.white
                    textDecoration = None.none
                    borderRadius = 4.px
                    marginBottom = 8.px
                    hover {
                        backgroundColor = Color("#303f9f")
                    }
                }

                ".nav-link.active" {
                    backgroundColor = Color("#303f9f")
                }

                ".card" {
                    backgroundColor = Color.white
                    borderRadius = 8.px
                    boxShadow = BoxShadow(0.px, 2.px, 4.px, Color.rgba(0, 0, 0, 0.1))
                }
            }
        }

        // Create App Context Provider
        AppContext.Provider {
            value = AppContextValue(
                client = client,
                scope = MainScope()
            )

            // Router Provider
            BrowserRouter {
                // Authentication Provider
                AuthProvider {
                    // Theme Provider (if needed)
                    ThemeProvider {
                        // Main App Component
                        App()
                    }
                }
            }
        }
    }))
}

// App Context
external interface AppContextValue {
    var client: ERPClient
    var scope: CoroutineScope
}

val AppContext = createContext<AppContextValue>()

// Auth Provider Component
external interface AuthProviderProps : PropsWithChildren

val AuthProvider = FC<AuthProviderProps> { props ->
    var isAuthenticated by useState(false)
    var currentUser by useState<User?>(null)
    var loading by useState(true)

    val client = useContext(AppContext).client
    val scope = useContext(AppContext).scope

    // Check authentication status on mount
    useEffectOnce {
        scope.launch {
            try {
                val token = window.localStorage.getItem("auth_token")
                if (token != null) {
                    client.setAuthToken(token)
                    currentUser = client.getCurrentUser()
                    isAuthenticated = true
                }
            } catch (e: Exception) {
                window.localStorage.removeItem("auth_token")
                isAuthenticated = false
                currentUser = null
            } finally {
                loading = false
            }
        }
    }

    if (loading) {
        LoadingSpinner()
    } else {
        AuthContext.Provider {
            value = AuthContextValue(
                isAuthenticated = isAuthenticated,
                currentUser = currentUser,
                login = { username, password ->
                    scope.launch {
                        try {
                            val response = client.login(username, password)
                            window.localStorage.setItem("auth_token", response.token)
                            client.setAuthToken(response.token)
                            currentUser = response.user
                            isAuthenticated = true
                        } catch (e: Exception) {
                            throw e
                        }
                    }
                },
                logout = {
                    window.localStorage.removeItem("auth_token")
                    client.setAuthToken(null)
                    currentUser = null
                    isAuthenticated = false
                }
            )
            +props.children
        }
    }
}

// Theme Provider Component
external interface ThemeProviderProps : PropsWithChildren

val ThemeProvider = FC<ThemeProviderProps> { props ->
    var isDarkMode by useState(false)

    // Load theme preference from localStorage
    useEffectOnce {
        val savedTheme = window.localStorage.getItem("theme")
        isDarkMode = savedTheme == "dark"
    }

    ThemeContext.Provider {
        value = ThemeContextValue(
            isDarkMode = isDarkMode,
            toggleTheme = {
                isDarkMode = !isDarkMode
                window.localStorage.setItem("theme", if (isDarkMode) "dark" else "light")
            }
        )
        +props.children
    }
}

// Loading Spinner Component
val LoadingSpinner = FC {
    div {
        css {
            display = Display.flex
            justifyContent = JustifyContent.center
            alignItems = AlignItems.center
            height = 100.vh
        }
        +"Loading..."
    }
}