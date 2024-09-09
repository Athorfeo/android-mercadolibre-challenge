package io.github.athorfeo.template.ui.search.query

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import io.github.athorfeo.template.R
import io.github.athorfeo.template.model.state.UiLogicState
import io.github.athorfeo.template.navigation.Screen
import io.github.athorfeo.template.ui.component.ErrorAlertDialog
import io.github.athorfeo.template.ui.component.Loading
import io.github.athorfeo.template.ui.component.SearchTextField
import io.github.athorfeo.template.ui.theme.ApplicationTheme

@Composable
fun QuerySearchRoute(
    navController: NavController,
    viewModel: QuerySearchViewModel = hiltViewModel()
) {
    val uiLogicState by viewModel.uiLogicState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()

    QuerySearchScreen(
        uiLogicState,
        query,
        viewModel::onDismissError,
        viewModel::onQueryChange
    ) {
        viewModel.onSearch { query ->
            val route = Screen.ResultSearch.buildNavigate(query)
            navController.navigate(route)
        }
    }
}

@Composable
fun QuerySearchScreen(
    uiLogicState: UiLogicState,
    query: String,
    onDismissErrorDialog: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    if (uiLogicState.isLoading) {
        Loading()
    } else {
        if(uiLogicState.exception != null) {
            ErrorAlertDialog(
                onDismissErrorDialog,
                onDismissErrorDialog,
                uiLogicState.exception.title,
                uiLogicState.exception.description
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Box {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White, shape = RoundedCornerShape(0.dp, 0.dp, 24.dp, 24.dp))
                    .padding(16.dp, 32.dp, 16.dp, 32.dp)) {
                    SearchTextField(query, onQueryChange, onSearch)
                    Text(
                        modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 8.dp),
                        text = stringResource(R.string.search_input_label),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth().padding(0.dp, 16.dp, 0.dp, 0.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.athorfeo),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    ApplicationTheme {
        val uiLogicState = UiLogicState()
        QuerySearchScreen(uiLogicState, "", {}, {}, {})
    }
}
