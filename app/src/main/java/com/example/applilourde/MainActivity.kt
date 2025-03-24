package com.example.applilourde

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.applilourde.data.repository.DisponibiliteRepository
import com.example.applilourde.data.repository.EnfantRepository
import com.example.applilourde.data.repository.ParentRepository
import com.example.applilourde.data.repository.ReservationRepository
import com.example.applilourde.ui.screens.*
import com.example.applilourde.ui.theme.AppliLourdeTheme

class MainActivity : ComponentActivity() {
    // Repositories
    private val parentRepository = ParentRepository()
    private val enfantRepository = EnfantRepository()
    private val reservationRepository = ReservationRepository()
    private val disponibiliteRepository = DisponibiliteRepository()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppliLourdeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(navController = navController, startDestination = "login") {
                        // Écran de connexion
                        composable("login") {
                            LoginScreen(
                                parentRepository = parentRepository,
                                onLoginSuccess = { parentId ->
                                    navController.navigate("home/$parentId") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToInscription = {
                                    navController.navigate("inscription")
                                }
                            )
                        }
                        
                        // Écran d'inscription
                        composable("inscription") {
                            InscriptionScreen(
                                parentRepository = parentRepository,
                                onInscriptionSuccess = { parentId ->
                                    navController.navigate("home/$parentId") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateBack = {
                                    navController.navigateUp()
                                }
                            )
                        }
                        
                        // Écran d'accueil
                        composable(
                            route = "home/{parentId}",
                            arguments = listOf(navArgument("parentId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val parentId = backStackEntry.arguments?.getString("parentId") ?: ""
                            
                            HomeScreen(
                                parentId = parentId,
                                parentRepository = parentRepository,
                                enfantRepository = enfantRepository,
                                reservationRepository = reservationRepository,
                                onNavigateToReservation = { enfantId ->
                                    navController.navigate("reservation/$enfantId")
                                },
                                onNavigateToMesReservations = {
                                    navController.navigate("mes-reservations/$parentId")
                                }
                            )
                        }
                        
                        // Écran de réservation
                        composable(
                            route = "reservation/{enfantId}",
                            arguments = listOf(navArgument("enfantId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val enfantId = backStackEntry.arguments?.getString("enfantId") ?: ""
                            
                            ReservationScreen(
                                enfantId = enfantId,
                                enfantRepository = enfantRepository,
                                disponibiliteRepository = disponibiliteRepository,
                                reservationRepository = reservationRepository,
                                onNavigateBack = {
                                    navController.navigateUp()
                                }
                            )
                        }
                        
                        // Écran de consultation des réservations
                        composable(
                            route = "mes-reservations/{parentId}",
                            arguments = listOf(navArgument("parentId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val parentId = backStackEntry.arguments?.getString("parentId") ?: ""
                            
                            MesReservationsScreen(
                                parentId = parentId,
                                enfantRepository = enfantRepository,
                                reservationRepository = reservationRepository,
                                onNavigateBack = {
                                    navController.navigateUp()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}