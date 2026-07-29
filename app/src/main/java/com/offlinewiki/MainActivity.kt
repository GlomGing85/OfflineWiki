package com.offlinewiki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.compose.viewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Settings
import com.offlinewiki.data.AppDatabase
import com.offlinewiki.data.WikiRepository
import com.offlinewiki.ui.*

class MainActivity : ComponentActivity() {
    private val repository by lazy {
        WikiRepository(AppDatabase.getDatabase(this), this)
    }
    private val mainViewModel: MainViewModel by viewModels { MainViewModelFactory(repository) }
    private val folderViewModel: FolderViewModel by viewModels { FolderViewModelFactory(repository) }
    private val downloadViewModel: DownloadViewModel by viewModels { DownloadViewModelFactory(repository) }
    private val searchViewModel: SearchViewModel by viewModels { SearchViewModelFactory(repository) }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 42 && resultCode == android.app.Activity.RESULT_OK) {
            data?.data?.let { handleSdSelection(it) }
        }
    }

    private fun handleSdSelection(uri: android.net.Uri?) {
        uri?.let {
            val takeFlags = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri, takeFlags)
            val settingsVM = SettingsViewModel(com.offlinewiki.data.StoragePreferences(this))
            // Get path from document URI
            val path = uri.path ?: uri.toString()
            settingsVM.setSdPath(path)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF6FF7F7),
                    onPrimary = Color(0xFF003737),
                    primaryContainer = Color(0xFF005151),
                    onPrimaryContainer = Color(0xFF6FF7F7),
                    secondary = Color(0xFFB1CCCC),
                    onSecondary = Color(0xFF1D3232),
                    background = Color(0xFF0F1210),
                    onBackground = Color(0xFFE0E3DE),
                    surface = Color(0xFF0F1210),
                    onSurface = Color(0xFFE0E3DE)
                )
            ) {
                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry?.destination?.route

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Offline Wiki") },
                            actions = {
                                IconButton(onClick = { navController.navigate("settings") }) {
                                    Icon(androidx.compose.material.icons.Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.primary)
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                titleContentColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentRoute == "articles",
                                onClick = { navController.navigate("articles") { popUpTo("articles") { inclusive = true } } },
                                icon = { Icon(androidx.compose.material.icons.Icons.Default.Article, contentDescription = "Articles") },
                                label = { Text("Articles") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == "folders",
                                onClick = { navController.navigate("folders") },
                                icon = { Text("📁") },
                                label = { Text("Folders") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == "search",
                                onClick = { navController.navigate("search") },
                                icon = { Text("🔍") },
                                label = { Text("Search") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == "queue",
                                onClick = { navController.navigate("queue") },
                                icon = { Text("⬇") },
                                label = { Text("Queue") }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavHost(
                            navController = navController,
                            startDestination = "articles",
                            modifier = Modifier.fillMaxSize()
                        ) {
                            composable("articles") {
                                ArticleListScreen(
                                    folderId = null,
                                    viewModel = mainViewModel,
                                    onNavigateToArticle = { id -> navController.navigate("article/$id") },
                                    onNavigateToFolders = { navController.navigate("folders") }
                                )
                            }
                            composable("folder/{folderId}") { backStackEntry ->
                                val folderId = backStackEntry.arguments?.getString("folderId")
                                ArticleListScreen(
                                    folderId = folderId,
                                    viewModel = mainViewModel,
                                    onNavigateToArticle = { id -> navController.navigate("article/$id") },
                                    onNavigateToFolders = { navController.navigate("folders") }
                                )
                            }
                            composable("folders") {
                                FolderScreen(
                                    viewModel = folderViewModel,
                                    onNavigateToFolder = { folderId ->
                                        if (folderId != null) navController.navigate("folder/$folderId")
                                        else navController.navigate("articles")
                                    }
                                )
                            }
                            composable("search") {
                                SearchScreen(
                                    viewModel = searchViewModel,
                                    onNavigateToArticle = { id -> navController.navigate("article/$id") }
                                )
                            }
                            composable("queue") {
                                DownloadQueueScreen(
                                    viewModel = downloadViewModel,
                                    onDownloadImage = { articleId -> navController.navigate("article/$articleId") }
                                )
                            }
                            composable("settings") {
                                val settingsVM: SettingsViewModel = androidx.lifecycle.compose.viewModel(
                                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                                        @Suppress("UNCHECKED_CAST")
                                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                            return SettingsViewModel(com.offlinewiki.data.StoragePreferences(this@MainActivity)) as T
                                        }
                                    }
                                )
                                SettingsScreen(
                                    viewModel = settingsVM,
                                    onBack = { navController.popBackStack() },
                                    onSelectSdPath = {
                                        val intent = android.content.Intent(android.content.Intent.ACTION_OPEN_DOCUMENT_TREE)
                                        startActivityForResult(intent, 42)
                                    }
                                )
                            }
                            composable("article/{articleId}") { backStackEntry ->
                                val articleId = backStackEntry.arguments?.getString("articleId") ?: return@composable
                                val detailViewModel: ArticleDetailViewModel = androidx.lifecycle.compose.viewModel(
                                    factory = ArticleDetailViewModelFactory(repository, articleId)
                                )
                                ArticleDetailScreen(
                                    articleId = articleId,
                                    viewModel = detailViewModel,
                                    onBack = { navController.popBackStack() },
                                    onDownloadImages = { /* trigger image download */ }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
