package com.Notes.notes

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

object Routes {
    const val MAIN = "main"
    const val CREATE_TYPE = "create_type"
    const val NORMAL_EDITOR = "normal_editor"
    const val QA_EDITOR = "qa_editor"
    const val DETAIL = "detail/{noteId}"
    const val PRACTICE = "practice"
    const val SETTINGS = "settings"

    const val SPLASH = "splash"
    fun detailRoute(noteId: Int): String {
        return "detail/$noteId"
    }
}

@Composable
fun NotesApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {

            SplashScreen(

                onSplashFinished = {

                    navController.navigate(Routes.MAIN) {

                        popUpTo(Routes.SPLASH) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.MAIN) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = stringResource(R.string.logo_desc)
            )
            MainNotesScreen(
                onAddClick = {
                    navController.navigate(Routes.CREATE_TYPE)
                },
                onNoteClick = { noteId ->
                    navController.navigate(Routes.detailRoute(noteId))
                },
                onPracticeClick = {
                    navController.navigate(Routes.PRACTICE)
                }
            )
        }

        composable(Routes.CREATE_TYPE) {
            CreateNoteTypeScreen(
                onNormalClick = {
                    navController.navigate(Routes.NORMAL_EDITOR)
                },
                onQaClick = {
                    navController.navigate(Routes.QA_EDITOR)
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        composable(Routes.NORMAL_EDITOR) {
            NormalNoteEditorScreen(
                onSaved = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.MAIN) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        composable(Routes.QA_EDITOR) {
            QaNoteEditorScreen(
                onSaved = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.MAIN) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0

            NoteDetailScreen(
                noteId = noteId,
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        composable(Routes.PRACTICE) {
            PracticeScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
    }
}