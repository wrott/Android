/*
 * Copyright (c) 2020 DuckDuckGo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duckduckgo.app.onboarding.store

import com.duckduckgo.app.CoroutineTestRule
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AppUserStageStoreTest {

    @get:Rule
    var coroutineRule = CoroutineTestRule()

    private val userStageDao: UserStageDao = mock()
    private val testee = AppUserStageStore(userStageDao, coroutineRule.testDispatcherProvider)

    @Test
    fun whenGetUserAppStageThenReturnCurrentStage() = runTest {
        givenCurrentStage(AppStage.DAX_ONBOARDING)

        val userAppStage = testee.getUserAppStage()

        assertEquals(AppStage.DAX_ONBOARDING, userAppStage)
    }

    @Test
    fun whenStageNewCompletedThenStageDaxOnboardingReturned() = runTest {
        givenCurrentStage(AppStage.NEW)

        val nextStage = testee.stageCompleted(AppStage.NEW)

        assertEquals(AppStage.DAX_ONBOARDING, nextStage)
    }

    @Test
    fun whenStageDaxOnboardingCompletedThenStageEstablishedReturned() = runTest {
        givenCurrentStage(AppStage.DAX_ONBOARDING)

        val nextStage = testee.stageCompleted(AppStage.DAX_ONBOARDING)

        assertEquals(AppStage.ESTABLISHED, nextStage)
    }

    @Test
    fun whenStageEstablishedCompletedThenStageEstablishedReturned() = runTest {
        givenCurrentStage(AppStage.ESTABLISHED)

        val nextStage = testee.stageCompleted(AppStage.ESTABLISHED)

        assertEquals(AppStage.ESTABLISHED, nextStage)
    }

    @Test
    fun whenMoveToStageThenUpdateUserStageInDao() = runTest {
        testee.moveToStage(AppStage.DAX_ONBOARDING)
        verify(userStageDao).updateUserStage(AppStage.DAX_ONBOARDING)
    }

    private suspend fun givenCurrentStage(appStage: AppStage) {
        whenever(userStageDao.currentUserAppStage()).thenReturn(UserStage(appStage = appStage))
    }
}
