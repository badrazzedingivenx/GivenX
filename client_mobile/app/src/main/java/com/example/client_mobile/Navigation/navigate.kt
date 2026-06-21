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

    // Decide the entry screen based on persisted state — no Splash needed.
    val startDestination = when {
        !TokenManager.hasSeenOnboarding() -> Route.Onboarding.route
        !TokenManager.isLoggedIn()        -> Route.Login.route
        else                              -> Route.MainHome.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
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

        // ─── 1. Onboarding → Login (pop Onboarding so back-button skips it) ─
        composable(Route.Onboarding.route) {
            ScreenSwipeInfo(
                onNavigateToLogin = { _: String ->
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // ─── 2. Login ───────────────────────────────────────────────────────
        //   • "Créer un compte" → AccountType screen
        //   • Successful auth   → MainHome (clear whole backstack)
        composable(Route.Login.route) {
            LoginScreen(
                userType = "user",
                onNavigateToSignup = { _: String ->
                    navController.navigate(Route.AccountType.route)
                },
                onNavigateToLawyerHome = {
                    navController.navigate(Route.MainHome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToUserHome = {
                    navController.navigate(Route.MainHome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ─── 3. AccountType (Role Selection) ─────────────────────────────────
        //   • "Utilisateur" → CreeAccountUser
        //   • "Avocat"      → CreeAccountAvocat
        //   NOTE: TypeCompteScreen does not currently expose an onNavigateToLogin
        //   callback in its UI, so the "Connect" action from this screen requires
        //   the system back button, which pops back to Login naturally.
        composable(Route.AccountType.route) {
            TypeCompteScreen(
                showBackground = true,
                onNavigateToRegister = { userType: String ->
                    when (userType) {
                        "lawyer" -> navController.navigate(Route.CreateAvocat.route)
                        else     -> navController.navigate(Route.CreateUser.route)
                    }
                }
            )
        }

        // ─── 4a. CreeAccountUser ─────────────────────────────────────────────
        //   • "Connect" → Login (clear auth backstack)
        //   • Successful registration → MainHome
        composable(Route.CreateUser.route) {
            CreeUserScreen(
                onNavigateToLogin = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Route.MainHome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ─── 4b. CreeAccountAvocat ───────────────────────────────────────────
        //   • "Connect" → Login (clear auth backstack)
        //   • Successful registration → MainHome
        composable(Route.CreateAvocat.route) {
            CreeAvocatScreen(
                onNavigateToLogin = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Route.MainHome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ─── Legacy: Login with userType arg (kept for backward compat) ──────
        composable(
            route = "Login/{userType}",
            arguments = listOf(navArgument("userType") { type = NavType.StringType })
        ) { backStackEntry ->
            val typeArg = backStackEntry.arguments?.getString("userType") ?: "user"
            LoginScreen(
                userType = typeArg,
                onNavigateToSignup = { _: String ->
                    navController.navigate(Route.AccountType.route)
                },
                onNavigateToLawyerHome = {
                    navController.navigate(Route.MainHome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToUserHome = {
                    navController.navigate(Route.MainHome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ─── Legacy: Shared registration (kept for backward compat) ──────────
        composable(
            route = Route.Register.route,
            arguments = listOf(navArgument("userType") { type = NavType.StringType })
        ) { backStackEntry ->
            val userType = backStackEntry.arguments?.getString("userType") ?: "user"
            RegistrationScreen(
                userType = userType,
                onNavigateBack = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                },
                onNavigateToUserHome = {
                    navController.navigate(Route.MainHome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToLawyerHome = {
                    navController.navigate(Route.MainHome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ─── Legacy: TypeCompte route alias ───────────────────────────────────
        composable("TypeCompte") {
            TypeCompteScreen(
                showBackground = true,
                onNavigateToRegister = { userType: String ->
                    when (userType) {
                        "lawyer" -> navController.navigate(Route.CreateAvocat.route)
                        else     -> navController.navigate(Route.CreateUser.route)
                    }
                }
            )
        }

        // ═════════════════════════════════════════════════════════════════════
        // Below: All existing post-auth routes — UNCHANGED
        // ═════════════════════════════════════════════════════════════════════

        // 5. Unified Main Home (Social Feed + Role Dashboard)
        composable(Route.MainHome.route) {
            val isLawyer = TokenManager.getUserType() == "lawyer"
            val lawyerId = TokenManager.getLawyerId()
            val clientId = TokenManager.getClientId()
            MainDashboardHost(
                isLawyer                 = isLawyer,
                onNavigateToLawyerProfile = { navController.navigate(Route.AvocatProfile.route) { launchSingleTop = true } },
                onNavigateToNotifications = { navController.navigate(if (isLawyer) Route.Notifications.createRoute("lawyer") else Route.Notifications.createRoute("user")) },
                onNavigateToChat          = { convId -> navController.navigate(Route.Chat.createRoute(convId)) },
                onNavigateToRequests = { navController.navigate(Route.LawyerRequests.route) },
                onNavigateToReservations = { navController.navigate(Route.LawyerReservations.route) },
                onNavigateToPayments      = { navController.navigate(Route.LawyerPayments.createRoute(lawyerId)) },
                onNavigateToCreator       = { navController.navigate(Route.LawyerCreator.route) { launchSingleTop = true } },
                onNavigateToUserProfile   = { navController.navigate(Route.UserProfile.route) { launchSingleTop = true } },
                onNavigateToAbout         = { navController.navigate(Route.About.route) },
                onNavigateToLawyerDetail  = { lawyerId -> navController.navigate(Route.LawyerDetail.createRoute(lawyerId)) },
                onNavigateToCategory      = { domaine -> navController.navigate(Route.LawyerList.createRoute(android.net.Uri.encode(domaine))) },
                onNavigateToAppointments  = { navController.navigate(Route.Appointments.route) },
                onNavigateToClientReservations = { navController.navigate(Route.ClientReservations.route) },
                onNavigateToDocuments     = { navController.navigate(Route.DocumentVault.route) },
                onNavigateToFacturation   = { navController.navigate(Route.Billing.createRoute(clientId)) },
                onNavigateToDossier       = { caseId -> navController.navigate(Route.DossierDetail.createRoute(caseId)) }
            )
        }

        // Legacy aliases so existing back-stack entries keep working
        composable(Route.UserHome.route) {
            val clientId = TokenManager.getClientId()
            UserDashboardHost(
                onNavigateToProfile       = { navController.navigate(Route.UserProfile.route) { launchSingleTop = true } },
                onNavigateToAbout         = { navController.navigate(Route.About.route) },
                onNavigateToLawyerDetail  = { lawyerId -> navController.navigate(Route.LawyerDetail.createRoute(lawyerId)) },
                onNavigateToCategory      = { domaine -> navController.navigate(Route.LawyerList.createRoute(android.net.Uri.encode(domaine))) },
                onNavigateToNotifications = { navController.navigate(Route.Notifications.createRoute("user")) },
                onNavigateToChat          = { convId -> navController.navigate(Route.Chat.createRoute(convId)) },
                onNavigateToAppointments  = { navController.navigate(Route.Appointments.route) },
                onNavigateToDocuments     = { navController.navigate(Route.DocumentVault.route) },
                onNavigateToFacturation   = { navController.navigate(Route.Billing.createRoute(clientId)) },
                onNavigateToDossier       = { caseId -> navController.navigate(Route.DossierDetail.createRoute(caseId)) }
            )
        }

        composable(Route.LawyerRequests.route) {
            LawyerRequestsScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.LawyerReservations.route) {
            LawyerReservationsScreen(
                onBack = { navController.popBackStack() },
                onChat = { resId -> navController.navigate(Route.ReservationChat.createRoute(resId)) }
            )
        }

        composable(
            route = Route.LawyerPayments.route,
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

        composable(Route.AvocatProfile.route) {
            AvocatProfile(
                onBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate(Route.EditLawyerProfile.route) },
                onLogout = {
                    UserService.signOut()
                    navController.navigate(Route.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.EditLawyerProfile.route) {
            EditLawyerProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // 6. User Home / Profile
        composable(Route.UserProfile.route) {
            UserProfileScreen(
                onBack = { navController.popBackStack() },
                onLogOut = {
                    // Use role before clearing so we route to the right login screen
                    UserService.signOut()
                    navController.navigate(Route.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToEdit = { navController.navigate(Route.EditUserProfile.route) },
                onNavigateToDocuments = { navController.navigate(Route.DocumentVault.route) }
            )
        }

        composable(Route.EditUserProfile.route) {
            EditUserProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Route.DossierDetail.route,
            arguments = listOf(navArgument("caseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getString("caseId") ?: "HAQ-2024-0312"
            DossierDetailScreen(
                caseId = caseId,
                onBack = { navController.popBackStack() },
                onNavigateToChat = { convId -> navController.navigate(Route.Chat.createRoute(convId)) }
            )
        }

        composable(Route.Appointments.route) { AppointmentsScreen(onBack = { navController.popBackStack() }) }
        composable(Route.ClientReservations.route) { ClientReservationsScreen(onBack = { navController.popBackStack() }) }
        composable(Route.DocumentVault.route) { DocumentVaultScreen(onBack = { navController.popBackStack() }) }
        composable(
            route = Route.Billing.route,
            arguments = listOf(navArgument("clientId") { type = NavType.IntType; defaultValue = -1 })
        ) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getInt("clientId") ?: -1
            PaymentScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Route.About.route) { AboutScreen(onBack = { navController.popBackStack() }) }

        composable(
            route = Route.Notifications.route,
            arguments = listOf(navArgument("userType") { type = NavType.StringType })
        ) { backStackEntry ->
            val isLawyer = backStackEntry.arguments?.getString("userType") == "lawyer"
            NotificationScreen(isLawyer = isLawyer, onBack = { navController.popBackStack() })
        }

        composable(
            route = Route.LawyerList.route,
            arguments = listOf(navArgument("domaine") { type = NavType.StringType })
        ) { backStackEntry ->
            val domaine = backStackEntry.arguments?.getString("domaine") ?: ""
            LawyerListScreen(domaine = domaine, onBack = { navController.popBackStack() }, onNavigateToDetail = { id -> navController.navigate(Route.LawyerDetail.createRoute(id)) })
        }

        composable(
            route = Route.LawyerDetail.route,
            arguments = listOf(navArgument("lawyerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lawyerId = backStackEntry.arguments?.getString("lawyerId") ?: ""
            LawyerDetailScreen(
                lawyerId = lawyerId,
                onBack = { navController.popBackStack() },
                onNavigateToChat = { convId -> navController.navigate(Route.Chat.createRoute(convId)) }
            )
        }

        composable(
            route = Route.ReservationChat.route,
            arguments = listOf(navArgument("reservationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reservationId = backStackEntry.arguments?.getString("reservationId") ?: ""
            ReservationChatScreen(
                reservationId = reservationId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Route.Chat.route,
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            ChatScreen(conversationId = conversationId, isLawyer = false, onBack = { navController.popBackStack() })
        }
    }
}
