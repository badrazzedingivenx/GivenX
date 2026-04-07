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

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "Onboarding",
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

        // 5. Lawyer Home / Profile
        composable("LawyerHome") {
            LawyerDashboardHost(
                fullName = LawyerSession.fullName,
                speciality = LawyerSession.title,
                profileImageUri = LawyerSession.profileImageUri,
                onNavigateToProfile = { navController.navigate("AvocatProfile") },
                onNavigateToNotifications = { navController.navigate("Notifications/lawyer") },
                onNavigateToChat = { convId -> navController.navigate("Chat/$convId") },
                onNavigateToRequests = { navController.navigate("LawyerRequests") },
                onNavigateToPayments = { navController.navigate("LawyerPayments") }
            )
        }

        composable("LawyerRequests") {
            LawyerRequestsScreen(onBack = { navController.popBackStack() })
        }

        composable("LawyerPayments") {
            LawyerPaymentsScreen(onBack = { navController.popBackStack() })
        }

        composable("AvocatProfile") {
            AvocatProfile(
                fullName = LawyerSession.fullName,
                title = LawyerSession.title,
                email = LawyerSession.email,
                phone = LawyerSession.phone,
                address = LawyerSession.address,
                bio = LawyerSession.bio,
                profileImageUri = LawyerSession.profileImageUri,
                onBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate("EditLawyerProfile") }
            )
        }

        composable("EditLawyerProfile") {
            EditLawyerProfileScreen(
                initialName = LawyerSession.fullName,
                initialTitle = LawyerSession.title,
                initialEmail = LawyerSession.email,
                initialPhone = LawyerSession.phone,
                initialAddress = LawyerSession.address,
                initialBio = LawyerSession.bio,
                initialSpecs = LawyerSession.specializations,
                initialImageUri = LawyerSession.profileImageUri,
                onBack = { navController.popBackStack() },
                onSave = { name, title, email, phone, address, bio, specs, imageUri ->
                    LawyerSession.updateProfile(name, title, email, phone, address, bio, specs, imageUri)
                }
            )
        }

        // 6. User Home / Profile
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
                userName = UserSession.name,
                userEmail = UserSession.email,
                userPhone = UserSession.phone,
                userAddress = UserSession.address,
                profileImageUri = UserSession.profileImageUri,
                onBack = { navController.popBackStack() },
                onLogOut = {
                    navController.navigate("Login/user") { popUpTo(0) { inclusive = true } }
                },
                onNavigateToEdit = { navController.navigate("EditUserProfile") },
                onNavigateToDocuments = { navController.navigate("DocumentVault") }
            )
        }

        composable("EditUserProfile") {
            EditUserProfileScreen(
                initialName = UserSession.name,
                initialEmail = UserSession.email,
                initialPhone = UserSession.phone,
                initialAddress = UserSession.address,
                initialImageUri = UserSession.profileImageUri,
                onBack = { navController.popBackStack() },
                onSave = { name, email, phone, address, imageUri ->
                    UserSession.updateProfile(name, email, phone, address, imageUri)
                }
            )
        }

        composable("Appointments") { AppointmentsScreen(onBack = { navController.popBackStack() }) }
        composable("DocumentVault") { DocumentVaultScreen(onBack = { navController.popBackStack() }) }
        composable("Billing") { BillingScreen(onBack = { navController.popBackStack() }) }
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
            val lawyerId = backStackEntry.arguments?.getString("lawyerId") ?: "1"
            LawyerDetailScreen(lawyerId = lawyerId, onBack = { navController.popBackStack() }, onNavigateToChat = { id ->
                val lawyer = sampleLawyers.find { it.id == id } ?: sampleLawyers.first()
                val conv = ConversationRepository.getOrCreate(lawyerId = id, lawyerName = lawyer.name, lawyerSpecialty = lawyer.specialty, clientName = UserSession.name)
                navController.navigate("Chat/${conv.id}")
            })
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
