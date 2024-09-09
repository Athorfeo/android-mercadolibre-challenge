package io.github.athorfeo.template.ui.item.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import io.github.athorfeo.template.R
import io.github.athorfeo.template.model.Item
import io.github.athorfeo.template.model.SalePriceItem
import io.github.athorfeo.template.model.state.ItemState
import io.github.athorfeo.template.ui.theme.ApplicationTheme

@Composable
fun DetailItemRoute(
    viewModel: DetailItemViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DetailItemScreen(
        uiState,
        viewModel::onOpenInBrowser
    )
}

@Composable
fun DetailItemScreen(
    uiState: ItemState,
    onOpenUrlInBrowser: () ->  Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
    ) {
        uiState.item?.let { item ->
            ImageDetailItemScreen(item)
            ContentDetailItemScreen(item, onOpenUrlInBrowser)
        }
    }
}

@Composable
fun ColumnScope.ImageDetailItemScreen(
    item: Item
){
    Box(
        modifier = Modifier
            .padding(0.dp, 16.dp, 0.dp, 0.dp)
            .align(Alignment.CenterHorizontally)
            .shadow(elevation = 5.dp, RoundedCornerShape(8.dp))
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

@Composable
fun ContentDetailItemScreen(
    item: Item,
    onOpenUrlInBrowser: () ->  Unit
){
    Box(
        modifier = Modifier
            .padding(0.dp, 16.dp, 0.dp, 0.dp)
            .shadow(elevation = 10.dp, RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp))
            .background(color = Color.White, RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp))
            .fillMaxHeight()
    ) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.primaryContainer,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(stringResource(R.string.title_item), style = MaterialTheme.typography.bodySmall)
            Text(item.title)

            Text(
                stringResource(R.string.price_item),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 0.dp)
            )
            Row {
                Text(
                    text = "${stringResource(R.string.money_symbol)} ${item.price}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = " ${item.currencyId}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                stringResource(R.string.quantity_available_item),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 0.dp)
            )
            Text(
                text = "${item.availableQuantity}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Button(
                modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 0.dp).fillMaxWidth(),
                onClick = onOpenUrlInBrowser
            ) {
                Text(stringResource(R.string.open_browser_item))
            }
        }

    }
}

@Preview
@Composable
fun ItemScreenPreview() {
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
        val state = ItemState(item = item)
        DetailItemScreen(state, {})
    }
}
