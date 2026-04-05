package com.example.client_mobile.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.client_mobile.Screens.AvocatProfile
import com.example.client_mobile.Screens.CreeAvocatScreen
import com.example.client_mobile.Screens.CreeUserScreen
import com.example.client_mobile.Screens.AboutScreen
import com.example.client_mobile.Screens.LawyerDashboardHost
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
                onNavigateToProfile = { navController.navigate("AvocatProfile") }
            )
        }

        composable("AvocatProfile") {
            AvocatProfile(
                onBack = { navController.popBackStack() }
            )
        }

        composable("UserHome") {
            UserDashboardHost(
                onNavigateToProfile = { navController.navigate("UserProfile") },
                onNavigateToAbout = { navController.navigate("About") }
            )
        }

        composable("UserProfile") {
            UserProfileScreen(
                onBack = { navController.popBackStack() },
                onLogOut = {
                    navController.navigate("Login/user") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("About") {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
