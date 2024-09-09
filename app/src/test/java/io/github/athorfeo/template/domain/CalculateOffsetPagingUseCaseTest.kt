package io.github.athorfeo.template.domain

import io.mockk.MockKAnnotations
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.Before

class CalculateOffsetPagingUseCaseTest {
    private lateinit var usecase: CalculateOffsetPagingUseCase

    @Before
    fun before() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        usecase = CalculateOffsetPagingUseCase()
    }

    @After
    fun after(){
        unmockkAll()
    }

    @Test
    fun back_paging_test() {
        Assert.assertEquals(25, usecase.backPaging(50))
    }

    @Test
    fun next_paging_test() {
        Assert.assertEquals(175, usecase.nextPaging(150, 200))
        Assert.assertEquals(200, usecase.nextPaging(180, 200))
    }
}
