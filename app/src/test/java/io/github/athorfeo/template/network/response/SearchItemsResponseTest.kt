package io.github.athorfeo.template.network.response

import com.google.gson.annotations.SerializedName
import io.mockk.MockKAnnotations
import io.mockk.mockk
import io.mockk.unmockkAll
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.Before

class SearchItemsResponseTest {
    @Before
    fun before() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @After
    fun after(){
        unmockkAll()
    }

    @Test
    fun search_items_response_test() {
        val paging: PagingSearchItems = mockk()
        val results = listOf<ItemSearchItems>()
        val model = SearchItemsResponse(paging, results)

        Assert.assertEquals(paging, model.paging)
        Assert.assertEquals(results, model.results)
    }

    @Test
    fun paging_search_items_test() {
        val total = 0
        val primaryResults = 0
        val offset = 0
        val limit = 0

        val model = PagingSearchItems(total, primaryResults, offset, limit)

        Assert.assertEquals(total, model.total)
        Assert.assertEquals(primaryResults, model.primaryResults)
        Assert.assertEquals(offset, model.offset)
        Assert.assertEquals(limit, model.limit)
    }

    @Test
    fun item_search_items_to_domain_model_test() {
        val salePriceResultSearchItems = SalePriceResultSearchItems(
            "currencyId",
            10.0
        )
        val itemDataModel = ItemSearchItems(
            "id",
            "title",
            "permalink",
            "thumbnail",
            "currencyId",
            10.0,
            salePriceResultSearchItems,
            5
        )

        val itemDomainModel = itemDataModel.toDomainModel()

        Assert.assertEquals(itemDomainModel.id, itemDataModel.id)
        Assert.assertEquals(itemDomainModel.title, itemDataModel.title)
        Assert.assertEquals(itemDomainModel.permalink, itemDataModel.permalink)
        Assert.assertEquals(itemDomainModel.currencyId, itemDataModel.currencyId)
        Assert.assertEquals(itemDomainModel.price, itemDataModel.price, 0.0)
        Assert.assertEquals(itemDomainModel.salePrice.currencyId, itemDataModel.salePrice.currencyId)
        Assert.assertEquals(itemDomainModel.salePrice.amount, itemDataModel.salePrice.amount, 0.0)
        Assert.assertEquals(itemDomainModel.availableQuantity, itemDataModel.availableQuantity)
    }
}
