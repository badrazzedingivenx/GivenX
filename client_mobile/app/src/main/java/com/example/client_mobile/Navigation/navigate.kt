package com.example.client_mobile.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.client_mobile.Screens.AppointmentsScreen
import com.example.client_mobile.Screens.AvocatProfile
import com.example.client_mobile.Screens.BillingScreen
import com.example.client_mobile.Screens.ChatScreen
import com.example.client_mobile.Screens.ConversationRepository
import com.example.client_mobile.Screens.CreeAvocatScreen
import com.example.client_mobile.Screens.DocumentVaultScreen
import com.example.client_mobile.Screens.CreeUserScreen
import com.example.client_mobile.Screens.AboutScreen
import com.example.client_mobile.Screens.EditLawyerProfileScreen
import com.example.client_mobile.Screens.EditUserProfileScreen
import com.example.client_mobile.Screens.LawyerChatScreen
import com.example.client_mobile.Screens.LawyerDetailScreen
import com.example.client_mobile.Screens.LawyerListScreen
import com.example.client_mobile.Screens.LawyerDashboardHost
import com.example.client_mobile.Screens.NotificationScreen
import com.example.client_mobile.Screens.UserDashboardHost
import com.example.client_mobile.Screens.UserProfileScreen
import com.example.client_mobile.Screens.LoginScreen
import com.example.client_mobile.Screens.ScreenSwipeInfo
import com.example.client_mobile.Screens.TypeCompteScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "Onboarding") {
        
        // 1. Initial Onboarding Swipe
        composable("Onboarding") {
            ScreenSwipeInfo(
                onNavigateToLogin = { userType: String ->
                    navController.navigate("Login/$userType")
                }
            )
        }

        // 2. Manual Type Selection
        composable("TypeCompte") {
            TypeCompteScreen(
                showBackground = true,
                onNavigateToLogin = { userType: String ->
                    navController.navigate("Login/$userType")
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
                onNavigateToSignup = { selectedType: String ->
                    if (selectedType == "lawyer") navController.navigate("CreeAvocat")
                    else navController.navigate("CreeUser")
                },
                onNavigateToLawyerHome = { 
                    navController.navigate("LawyerHome") {
                        popUpTo("Login/{userType}") { inclusive = true }
                    }
                },
                onNavigateToUserHome = { 
                    navController.navigate("UserHome") {
                        popUpTo("Login/{userType}") { inclusive = true }
                    }
                }
            )
        }

        // 4. Registration Screens
        composable("CreeUser") {
            CreeUserScreen(
                onNavigateToLogin = { navController.navigate("Login/user") },
                onNavigateToHome = { 
                    navController.navigate("UserHome") {
                        popUpTo("CreeUser") { inclusive = true }
                    }
                }
            )
        }

        composable("CreeAvocat") {
            CreeAvocatScreen(
                onNavigateToLogin = { navController.navigate("Login/lawyer") },
                onNavigateToHome = { 
                    navController.navigate("LawyerHome") {
                        popUpTo("CreeAvocat") { inclusive = true }
                    }
                }
            )
        }

        // 5. Home / Profile Screens
        composable("LawyerHome") {
            LawyerDashboardHost(
                onNavigateToProfile = { navController.navigate("AvocatProfile") },
                onNavigateToNotifications = { navController.navigate("Notifications/lawyer") },
                onNavigateToChat = { convId -> navController.navigate("Chat/$convId") }
            )
        }

        composable("AvocatProfile") {
            AvocatProfile(
                onBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate("EditLawyerProfile") }
            )
        }

        composable("EditLawyerProfile") {
            EditLawyerProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("UserHome") {
            UserDashboardHost(
                onNavigateToProfile = { navController.navigate("UserProfile") },
                onNavigateToAbout = { navController.navigate("About") },
                onNavigateToLawyerDetail = { lawyerId -> navController.navigate("LawyerDetail/$lawyerId") },
                onNavigateToCategory = { domaine ->
                    navController.navigate("LawyerList/${android.net.Uri.encode(domaine)}")
                },
                onNavigateToNotifications = { navController.navigate("Notifications/user") },
                onNavigateToChat = { convId -> navController.navigate("Chat/$convId") },
                onNavigateToAppointments = { navController.navigate("Appointments") },
                onNavigateToDocuments = { navController.navigate("DocumentVault") },
                onNavigateToFacturation = { navController.navigate("Billing") }
            )
        }

        composable("UserProfile") {
            UserProfileScreen(
                onBack = { navController.popBackStack() },
                onLogOut = {
                    navController.navigate("Login/user") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToEdit = { navController.navigate("EditUserProfile") }
            )
        }

        composable("EditUserProfile") {
            EditUserProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("Appointments") {
<<<<<<< HEAD
            AppointmentsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("DocumentVault") {
            DocumentVaultScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("Billing") {
            BillingScreen(
                onBack = { navController.popBackStack() }
            )
=======
            AppointmentsScreen(onBack = { navController.popBackStack() })
        }

        composable("DocumentVault") {
            DocumentVaultScreen(onBack = { navController.popBackStack() })
        }

        composable("Billing") {
            BillingScreen(onBack = { navController.popBackStack() })
>>>>>>> developer_mobile
        }

        composable("About") {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "Notifications/{userType}",
            arguments = listOf(navArgument("userType") { type = NavType.StringType })
        ) { backStackEntry ->
            val isLawyer = backStackEntry.arguments?.getString("userType") == "lawyer"
            NotificationScreen(
                isLawyer = isLawyer,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "LawyerList/{domaine}",
            arguments = listOf(navArgument("domaine") { type = NavType.StringType })
        ) { backStackEntry ->
            val domaine = backStackEntry.arguments?.getString("domaine") ?: ""
            LawyerListScreen(
                domaine = domaine,
                onBack = { navController.popBackStack() },
                onNavigateToDetail = { lawyerId -> navController.navigate("LawyerDetail/$lawyerId") }
            )
        }

        composable(
            route = "LawyerDetail/{lawyerId}",
            arguments = listOf(navArgument("lawyerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lawyerId = backStackEntry.arguments?.getString("lawyerId") ?: "1"
            LawyerDetailScreen(
                lawyerId = lawyerId,
                onBack = { navController.popBackStack() },
                onNavigateToChat = { id ->
                    val lawyer = com.example.client_mobile.Screens.sampleLawyers.find { it.id == id }
                        ?: com.example.client_mobile.Screens.sampleLawyers.first()
                    val conv = ConversationRepository.getOrCreate(
                        lawyerId = id,
                        lawyerName = lawyer.name,
                        lawyerSpecialty = lawyer.specialty,
                        clientName = "Karim Bennani"
                    )
                    navController.navigate("Chat/${conv.id}")
                }
            )
        }

        composable(
            route = "Chat/{conversationId}",
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            ChatScreen(
                conversationId = conversationId,
                isLawyer = false,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "LawyerChat/{lawyerId}",
            arguments = listOf(navArgument("lawyerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lawyerId = backStackEntry.arguments?.getString("lawyerId") ?: "1"
            val lawyer = com.example.client_mobile.Screens.sampleLawyers.find { it.id == lawyerId }
                ?: com.example.client_mobile.Screens.sampleLawyers.first()
            val conv = ConversationRepository.getOrCreate(
                lawyerId = lawyerId,
                lawyerName = lawyer.name,
                lawyerSpecialty = lawyer.specialty,
                clientName = "Karim Bennani"
            )
            ChatScreen(
                conversationId = conv.id,
                isLawyer = false,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
