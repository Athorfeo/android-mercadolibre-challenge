package io.github.athorfeo.template.ui.search.result

import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import io.github.athorfeo.template.R
import io.github.athorfeo.template.data.repository.SearchItemsRepository
import io.github.athorfeo.template.model.Item
import io.github.athorfeo.template.model.SalePriceItem
import io.github.athorfeo.template.model.SearchedItemsPaging
import io.github.athorfeo.template.model.state.UiLogicState
import io.github.athorfeo.template.navigation.Screen
import io.github.athorfeo.template.ui.component.ErrorAlertDialog
import io.github.athorfeo.template.ui.component.Loading
import io.github.athorfeo.template.ui.theme.ApplicationTheme
import kotlinx.coroutines.launch

@Composable
fun ResultSearchRoute(
    navController: NavController,
    viewModel: ResultSearchViewModel = hiltViewModel()
) {
    val uiLogicState by viewModel.uiLogicState.collectAsStateWithLifecycle()
    val itemsPagingState by viewModel.itemsPagingState.collectAsStateWithLifecycle()

    ResultSearchScreen(
        viewModel.query,
        uiLogicState,
        itemsPagingState,
        viewModel::onDismissError,
        viewModel::onBackPaging,
        viewModel::onNextPaging
    ) { itemId ->
        val route = Screen.DetailItem.buildNavigate(itemId)
        navController.navigate(route)
    }
}
@Suppress("LongParameterList")
@Composable
fun ResultSearchScreen(
    query: String,
    uiLogicState: UiLogicState,
    itemsPagingState: SearchedItemsPaging,
    onDismissErrorDialog: () -> Unit,
    onBackPaging: (() -> Unit) -> Unit,
    onNextPaging: (() -> Unit) -> Unit,
    onGoDetail: (String) -> Unit
) {
    if (uiLogicState.isLoading) {
        Loading()
    } else {
        if (uiLogicState.exception != null) {
            ErrorAlertDialog(
                onDismissErrorDialog,
                onDismissErrorDialog,
                uiLogicState.exception.title,
                uiLogicState.exception.description
            )
        }

        val searchedItems = itemsPagingState.items
        val coroutineScope = rememberCoroutineScope()
        val listState = rememberLazyListState()

        LazyColumn(state = listState) {
            if (searchedItems.isEmpty()) {
                item {
                    CaptionResultSearchScreen(stringResource(R.string.no_items_found))
                }
            } else {
                item {
                    CaptionResultSearchScreen("${stringResource(R.string.result_search)} $query")
                }

                items(searchedItems.size) { index ->
                    val itemRow = searchedItems[index]
                    ItemResultSearchScreen(itemRow, onGoDetail)
                }

                item {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        thickness = 1.dp,
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    )
                }

                item {
                    PagingItemResultSearchScreen(
                        itemsPagingState.total,
                        itemsPagingState.offset,
                        { onBackPaging {
                            coroutineScope.launch { listState.animateScrollToItem(index = 0) }
                        }},
                        { onNextPaging {
                            coroutineScope.launch { listState.animateScrollToItem(index = 0) }
                        }}
                    )
                }
            }
        }
    }
}

@Composable
fun CaptionResultSearchScreen(text: String) {
    Column(
        modifier = Modifier
            .padding(16.dp, 0.dp, 16.dp, 0.dp)
            .shadow(elevation = 1.dp, RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun ItemResultSearchScreen(
    item: Item,
    onGoDetail: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 16.dp, 0.dp, 0.dp)
            .clickable { onGoDetail(item.id) },
    ) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.primaryContainer,
            thickness = 1.dp,
            modifier = Modifier
                .padding(16.dp, 0.dp, 16.dp, 0.dp)
                .fillMaxWidth()
        )
        Row(
            modifier = Modifier
                .padding(16.dp, 16.dp, 16.dp, 0.dp)
                .fillMaxWidth()
        ) {
            ImageItemResultSearchScreen(item)
            ContentItemResultSearchScreen(item)
        }
    }
}

@Composable
fun ImageItemResultSearchScreen(
    item: Item
) {
    Box(
        modifier = Modifier
            .shadow(elevation = 1.dp, RoundedCornerShape(8.dp))
            .background(color = Color.White, RoundedCornerShape(8.dp))
            .padding(0.dp, 8.dp, 0.dp, 8.dp)
    ) {
        AsyncImage(
            modifier = Modifier.width(80.dp).height(80.dp),
            model = item.thumbnail,
            contentDescription = null
        )
    }
}

private const val WEIGHT_CONTENT_ITEM = 0.6f

@Composable
fun RowScope.ContentItemResultSearchScreen(
    item: Item
) {
    Column(modifier = Modifier.weight(WEIGHT_CONTENT_ITEM)) {
        Text(
            modifier = Modifier.padding(4.dp, 0.dp, 0.dp, 0.dp),
            text = item.title,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            modifier = Modifier.padding(4.dp, 16.dp, 0.dp, 0.dp),
            text = "$ ${item.price}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            modifier = Modifier.padding(4.dp, 16.dp, 0.dp, 0.dp),
            text = "${item.availableQuantity} ${stringResource(R.string.item_available)}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun PagingItemResultSearchScreen(
    total: Int,
    offset: Int,
    onBackPaging: () -> Unit,
    onNextPaging: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledIconButton(
            modifier = Modifier.padding(16.dp),
            onClick = onBackPaging,
            enabled = offset >= SearchItemsRepository.OFFSET_ITEMS_PAGING
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
        }
        Spacer(modifier = Modifier.weight(1f))

        FilledIconButton(
            modifier = Modifier.padding(16.dp),
            onClick = onNextPaging,
            enabled = (offset + SearchItemsRepository.OFFSET_ITEMS_PAGING) < total
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "")
        }
    }
}

@Preview
@Composable
fun ResultSearchScreenPreview() {
    ApplicationTheme {
        val item = Item(
            "",
            "Title",
            "",
            "http://http2.mlstatic.com/D_877779-MLA73646533395_122023-O.jpg",
            "",
            0.0,
            SalePriceItem("", 0.0),
            1,
        )
        val results = listOf(item)
        val uiLogicState =  UiLogicState()
        val paging = SearchedItemsPaging(items = results)
        ResultSearchScreen("Query", uiLogicState, paging, {}, {}, {}, {})
    }
}

@Preview
@Composable
fun NoItemsResultSearchScreenPreview() {
    ApplicationTheme {
        val uiLogicState =  UiLogicState()
        val paging = SearchedItemsPaging()
        ResultSearchScreen("Query", uiLogicState, paging, {}, {}, {}, {})
    }
}
