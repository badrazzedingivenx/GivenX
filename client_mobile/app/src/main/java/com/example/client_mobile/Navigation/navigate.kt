package com.example.client_mobile.Navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.client_mobile.screens.shared.*
import com.example.client_mobile.screens.user.*
import com.example.client_mobile.screens.lawyer.*
import com.example.client_mobile.screens.shared.RegistrationScreen
import com.example.client_mobile.network.TokenManager
import com.example.client_mobile.services.UserService
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // "Splash" is always the entry point. It validates any stored token against
    // the live API before navigating — no dashboard jump without an API response.
    NavHost(
        navController = navController,
        startDestination = "Splash",
        enterTransition = {
            fadeIn(animationSpec = tween(300)) +
                slideInHorizontally(animationSpec = tween(300)) { it / 4 }
        },
        exitTransition = {
            fadeOut(animationSpec = tween(200)) +
                slideOutHorizontally(animationSpec = tween(200)) { -it / 4 }
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) +
                slideInHorizontally(animationSpec = tween(300)) { -it / 4 }
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(200)) +
                slideOutHorizontally(animationSpec = tween(200)) { it / 4 }
        }
    ) {

        // 0. Splash — validates stored token via API, routes to Login or Dashboard
        composable("Splash") {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate("TypeCompte") {
                        popUpTo("Splash") { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate("Onboarding") {
                        popUpTo("Splash") { inclusive = true }
                    }
                },
                onNavigateToUserHome = {
                    navController.navigate("UserHome") {
                        popUpTo("Splash") { inclusive = true }
                    }
                },
                onNavigateToLawyerHome = {
                    navController.navigate("LawyerHome") {
                        popUpTo("Splash") { inclusive = true }
                    }
                }
            )
        }

        // 1. Initial Onboarding Swipe
        composable("Onboarding") {
            ScreenSwipeInfo(
                onNavigateToLogin = { userType: String ->
                    navController.navigate("Login/$userType")
                }
            )
        }

        // 2. Manual Type Selection (For Sign Up)
        composable("TypeCompte") {
            TypeCompteScreen(
                showBackground = true,
                onNavigateToRegister = { userType: String ->
                    navController.navigate("Register/$userType")
                }
            )
        }

        // 3. Login Screen
        composable(
            route = "Login/{userType}",
            arguments = listOf(navArgument("userType") { type = NavType.StringType })
        ) { backStackEntry ->
            val typeArg = backStackEntry.arguments?.getString("userType") ?: "user"
            LoginScreen(
                userType = typeArg,
                onNavigateToSignup = {
                    navController.navigate("TypeCompte")
                },
                onNavigateToLawyerHome = { 
                    navController.navigate("LawyerHome") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToUserHome = { 
                    navController.navigate("UserHome") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // 4. Registration Screens
        composable(
            route = "Register/{userType}",
            arguments = listOf(navArgument("userType") { type = NavType.StringType })
        ) { backStackEntry ->
            val userType = backStackEntry.arguments?.getString("userType") ?: "user"
            RegistrationScreen(
                userType = userType,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToUserHome = {
                    navController.navigate("UserHome") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToLawyerHome = {
                    navController.navigate("LawyerHome") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // 5. Lawyer Home / Profile
        composable("LawyerHome") {
            val lawyerId = TokenManager.getLawyerId()
            LawyerDashboardHost(
                onNavigateToProfile = { navController.navigate("AvocatProfile") { launchSingleTop = true } },
                onNavigateToNotifications = { navController.navigate("Notifications/lawyer") },
                onNavigateToChat = { convId -> navController.navigate("Chat/$convId") },
                onNavigateToRequests = { navController.navigate("LawyerRequests") },
                onNavigateToPayments = { navController.navigate("LawyerPayments?lawyerId=$lawyerId") },
                onNavigateToCreator  = { navController.navigate("LawyerCreatorStudio") { launchSingleTop = true } }
            )
        }

        composable("LawyerRequests") {
            LawyerRequestsScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = "LawyerPayments?lawyerId={lawyerId}",
            arguments = listOf(navArgument("lawyerId") { type = NavType.IntType; defaultValue = -1 })
        ) { backStackEntry ->
            val lawyerId = backStackEntry.arguments?.getInt("lawyerId") ?: -1
            // Use a specific Lawyer version of the payment screen or pass a flag
            PaymentScreen(
                onBack = { navController.popBackStack() },
                // You can pass the ID to the ViewModel or via a factory
                lawyerId = lawyerId
            )
        }

        composable("LawyerCreatorStudio") {
            LawyerCreatorManagementScreen(onBack = { navController.popBackStack() })
        }

        composable("AvocatProfile") {
            AvocatProfile(
                onBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate("EditLawyerProfile") },
                onLogout = {
                    UserService.signOut()
                    navController.navigate("Login/lawyer") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("EditLawyerProfile") {
            EditLawyerProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // 6. User Home / Profile
        composable("UserHome") {
            val clientId = TokenManager.getClientId()
            UserDashboardHost(
                onNavigateToProfile = { navController.navigate("UserProfile") { launchSingleTop = true } },
                onNavigateToAbout = { navController.navigate("About") },
                onNavigateToLawyerDetail = { lawyerId -> navController.navigate("LawyerDetail/$lawyerId") },
                onNavigateToCategory = { domaine ->
                    navController.navigate("LawyerList/${android.net.Uri.encode(domaine)}")
                },
                onNavigateToNotifications = { navController.navigate("Notifications/user") },
                onNavigateToChat = { convId -> navController.navigate("Chat/$convId") },
                onNavigateToAppointments = { navController.navigate("Appointments") },
                onNavigateToDocuments = { navController.navigate("DocumentVault") },
                onNavigateToFacturation = { navController.navigate("Billing?clientId=$clientId") },
                onNavigateToDossier = { caseId -> navController.navigate("DossierDetail/$caseId") }
            )
        }

        composable("UserProfile") {
            UserProfileScreen(
                onBack = { navController.popBackStack() },
                onLogOut = {
                    // Use role before clearing so we route to the right login screen
                    val role = TokenManager.getUserType()
                    UserService.signOut()
                    val dest = if (role == "lawyer") "Login/lawyer" else "Login/user"
                    navController.navigate(dest) { popUpTo(0) { inclusive = true } }
                },
                onNavigateToEdit = { navController.navigate("EditUserProfile") },
                onNavigateToDocuments = { navController.navigate("DocumentVault") }
            )
        }

        composable("EditUserProfile") {
            EditUserProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "DossierDetail/{caseId}",
            arguments = listOf(navArgument("caseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getString("caseId") ?: "HAQ-2024-0312"
            DossierDetailScreen(
                caseId = caseId,
                onBack = { navController.popBackStack() },
                onNavigateToChat = { convId -> navController.navigate("Chat/$convId") }
            )
        }

        composable("Appointments") { AppointmentsScreen(onBack = { navController.popBackStack() }) }
        composable("DocumentVault") { DocumentVaultScreen(onBack = { navController.popBackStack() }) }
        composable(
            route = "Billing?clientId={clientId}",
            arguments = listOf(navArgument("clientId") { type = NavType.IntType; defaultValue = -1 })
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getInt("clientId") ?: -1
            PaymentScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("About") { AboutScreen(onBack = { navController.popBackStack() }) }

        composable(
            route = "Notifications/{userType}",
            arguments = listOf(navArgument("userType") { type = NavType.StringType })
        ) { backStackEntry ->
            val isLawyer = backStackEntry.arguments?.getString("userType") == "lawyer"
            NotificationScreen(isLawyer = isLawyer, onBack = { navController.popBackStack() })
        }

        composable(
            route = "LawyerList/{domaine}",
            arguments = listOf(navArgument("domaine") { type = NavType.StringType })
        ) { backStackEntry ->
            val domaine = backStackEntry.arguments?.getString("domaine") ?: ""
            LawyerListScreen(domaine = domaine, onBack = { navController.popBackStack() }, onNavigateToDetail = { id -> navController.navigate("LawyerDetail/$id") })
        }

        composable(
            route = "LawyerDetail/{lawyerId}",
            arguments = listOf(navArgument("lawyerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lawyerId = backStackEntry.arguments?.getString("lawyerId") ?: ""
            LawyerDetailScreen(
                lawyerId = lawyerId,
                onBack = { navController.popBackStack() },
                onNavigateToChat = { convId -> navController.navigate("Chat/$convId") }
            )
        }

        composable(
            route = "Chat/{conversationId}",
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            ChatScreen(conversationId = conversationId, isLawyer = false, onBack = { navController.popBackStack() })
        }
    }
}
