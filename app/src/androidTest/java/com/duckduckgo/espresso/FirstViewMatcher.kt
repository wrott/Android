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

import android.view.View
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Factory
import org.hamcrest.Matcher

class CustomMatchers {
    companion object {
        fun firstView(): Matcher<View> {
            return object : BaseMatcher<View>() {
                var matchedBefore = false

                override fun describeTo(description: Description?) {
                    description?.appendText(" is the first view that comes along ")
                }

                override fun matches(item: Any?): Boolean {
                    return if (matchedBefore) {
                        false
                    } else {
                        matchedBefore = true
                        true
                    }
                }
            }
        }
    }
}
