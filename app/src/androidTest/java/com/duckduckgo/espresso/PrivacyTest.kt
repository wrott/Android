/*
 * Copyright (c) 2022 DuckDuckGo
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

package com.duckduckgo.espresso

import android.text.format.DateUtils
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.DriverAtoms.getText
import androidx.test.espresso.web.webdriver.DriverAtoms.webClick
import androidx.test.espresso.web.webdriver.Locator
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.duckduckgo.app.browser.BrowserActivity
import com.duckduckgo.app.browser.R
import com.duckduckgo.espresso.CustomMatchers.Companion.firstView
import okhttp3.internal.wait
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit
import androidx.test.espresso.Espresso

import androidx.test.espresso.IdlingResource




@RunWith(AndroidJUnit4::class)
class PrivacyTest {

    /**
     * Use [ActivityScenarioRule] to create and launch the activity under test before each test,
     * and close it after each test. This is a replacement for
     * [androidx.test.rule.ActivityTestRule].
     */
    @get:Rule
    var activityScenarioRule = activityScenarioRule<BrowserActivity>(BrowserActivity.intent(InstrumentationRegistry.getInstrumentation().targetContext, "https://senglehardt.com/test/privacy-protections/storage-partitioning-2/"))

    @Test @UserJourney
    fun privacyTest() {
        val waitingTime: Long = 10000

        // since we use a fake toolbar, we want to wait until the real one is visible
        onView(isRoot()).perform(waitForView(withId(R.id.browserMenu)))

        onWebView()
            .withElement(findElement(Locator.ID, "run")) // similar to onView(withId(...))
            .check(webMatches(getText(), containsString("Run Tests")))
            .perform(webClick())

        IdlingPolicies.setMasterPolicyTimeout(
            waitingTime * 2, TimeUnit.MILLISECONDS,
        );
        IdlingPolicies.setIdlingResourceTimeout(
            waitingTime * 2, TimeUnit.MILLISECONDS,
        );

        val idlingResource: IdlingResource = ElapsedTimeIdlingResource(waitingTime)
        Espresso.registerIdlingResources(idlingResource)

        onWebView()
            .withTimeout(30, TimeUnit.SECONDS)
            .withContextualElement(findElement(Locator.ID, "test-document.cookie")) // similar to onView(withId(...))
            .check(webMatches(getText(), containsString("document.cookie - pass")))


    }

}
