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

import androidx.test.espresso.Espresso.onView
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
import okhttp3.internal.wait
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class PrivacyTest {

    /**
     * Use [ActivityScenarioRule] to create and launch the activity under test before each test,
     * and close it after each test. This is a replacement for
     * [androidx.test.rule.ActivityTestRule].
     */
    @get:Rule
    var activityScenarioRule = activityScenarioRule<BrowserActivity>(BrowserActivity.intent(InstrumentationRegistry.getInstrumentation().targetContext, "https://senglehardt.com/test/privacy-protections/storage-partitioning/"))

    @Test @UserJourney
    fun browser_openPopUp() {
        // since we use a fake toolbar, we want to wait until the real one is visible
        onView(isRoot()).perform(waitForView(withId(R.id.browserMenu)))

        Thread.sleep(3000)
//+----------->DuckDuckGoWebView{id=2131230889, res-name=browserWebView, visibility=VISIBLE, width=1080, height=2042, has-focus=false, has-focusable=true, has-window-focus=true, is-clickable=true, is-enabled=true, is-focused=false, is-focusable=true, is-layout-requested=false, is-selected=false, layout-params=android.widget.FrameLayout$LayoutParams@9854b2b, tag=null, root-is-layout-requested=false, has-input-connection=false, x=0.0, y=0.0, child-count=0} ****MATCHES****
//+----------->DuckDuckGoWebView{id=2131230889, res-name=browserWebView, visibility=VISIBLE, width=1080, height=2042, has-focus=false, has-focusable=true, has-window-focus=true, is-clickable=true, is-enabled=true, is-focused=false, is-focusable=true, is-layout-requested=false, is-selected=false, layout-params=android.widget.FrameLayout$LayoutParams@37ea7f9, tag=null, root-is-layout-requested=false, has-input-connection=false, x=0.0, y=0.0, child-count=0} ****MATCHES****

        onWebView(allOf(withId(R.id.browserWebView), isCompletelyDisplayed()))
            .withElement(findElement(Locator.ID, "run")) // similar to onView(withId(...))
            .check(webMatches(getText(), containsString("Run Tests")))
            .perform(webClick())

        onWebView(allOf(withId(R.id.browserWebView), isCompletelyDisplayed()))
            .withElement(findElement(Locator.ID, "test-document.cookie")) // similar to onView(withId(...))
            .check(webMatches(getText(), containsString("false")))
            .withTimeout(30, TimeUnit.SECONDS)

    }

}
