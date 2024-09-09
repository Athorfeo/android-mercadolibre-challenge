package io.github.athorfeo.template.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.get
import io.github.athorfeo.template.navigation.Screen
import io.github.athorfeo.template.navigation.rootScreens
import io.github.athorfeo.template.ui.item.detail.DetailItemRoute
import io.github.athorfeo.template.ui.search.query.QuerySearchRoute
import io.github.athorfeo.template.ui.search.result.ResultSearchRoute
import io.github.athorfeo.template.ui.theme.ApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry?.destination?.route
    val isRootScreen = rootScreens.find { currentRoute?.contains(it.route) == true }
    var titleScreen by remember { mutableStateOf<Int?>(null) }
    val onUpdateTitleScreen by remember { mutableStateOf<(Int?) -> Unit>({ resourceId ->
        titleScreen = resourceId
    })}

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (isRootScreen == null) {
                TopAppBar(
                    title = { titleScreen?.let{ Text(text = stringResource(it)) } },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.QuerySearch.buildRoute(),
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.QuerySearch, onUpdateTitleScreen) { QuerySearchRoute(navController) }
            composable(Screen.ResultSearch, onUpdateTitleScreen) { ResultSearchRoute(navController) }
            composable(Screen.DetailItem, onUpdateTitleScreen) { DetailItemRoute() }
        }
    }
}

fun NavGraphBuilder.composable(
    screen: Screen,
    onUpdateTitle: (Int?) -> Unit,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(
        screen.buildRoute(),
        content = { backStackEntry ->
            onUpdateTitle(screen.title)
            content(backStackEntry)
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    ApplicationTheme {
        App()
    }
}
