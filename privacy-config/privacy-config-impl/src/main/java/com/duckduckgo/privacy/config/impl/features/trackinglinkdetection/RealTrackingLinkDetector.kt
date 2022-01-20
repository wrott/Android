/*
 * Copyright (c) 2021 DuckDuckGo
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

package com.duckduckgo.privacy.config.impl.features.trackinglinkdetection

import com.duckduckgo.app.global.UriString
import com.duckduckgo.di.scopes.AppScope
import com.duckduckgo.feature.toggles.api.FeatureToggle
import com.duckduckgo.privacy.config.api.PrivacyFeatureName
import com.duckduckgo.privacy.config.api.TrackingLinkDetector
import com.duckduckgo.privacy.config.api.TrackingLinkInfo
import com.duckduckgo.privacy.config.api.TrackingLinkType
import com.duckduckgo.privacy.config.impl.features.unprotectedtemporary.UnprotectedTemporary
import com.duckduckgo.privacy.config.store.features.trackinglinkdetection.TrackingLinkDetectionRepository
import com.squareup.anvil.annotations.ContributesBinding
import dagger.SingleInstanceIn
import javax.inject.Inject

@ContributesBinding(AppScope::class)
@SingleInstanceIn(AppScope::class)
class RealTrackingLinkDetector @Inject constructor(
    private val trackingLinkDetectionRepository: TrackingLinkDetectionRepository,
    private val featureToggle: FeatureToggle,
    private val unprotectedTemporary: UnprotectedTemporary
) : TrackingLinkDetector {

    private var lastExtractedUrl: String? = null

    override var lastTrackingLinkInfo: TrackingLinkInfo? = null

    override var isProcessingTrackingLink = false

    override fun isAnException(url: String): Boolean {
        return matches(url) || unprotectedTemporary.isAnException(url)
    }

    private fun matches(url: String): Boolean {
        return trackingLinkDetectionRepository.exceptions.any { UriString.sameOrSubdomain(url, it.domain) }
    }

    override fun extractCanonicalFromTrackingLink(url: String): TrackingLinkType? {
        if (featureToggle.isFeatureEnabled(PrivacyFeatureName.TrackingLinkDetectionFeatureName()) == false) return null
        if (url == lastExtractedUrl || isProcessingTrackingLink) return null

        val extractedUrl = extractCanonical(url)

        lastExtractedUrl = extractedUrl

        extractedUrl?.let {
            return if (isAnException(extractedUrl)) {
                null
            } else {
                lastTrackingLinkInfo = TrackingLinkInfo(trackingLink = url)
                TrackingLinkType.ExtractedTrackingLink(extractedUrl = extractedUrl)
            }
        }

        if (urlContainsTrackingKeyword(url)) {
            return if (isAnException(url)) {
                null
            } else {
                TrackingLinkType.CloakedTrackingLink(trackingUrl = url)
            }
        }
        return null
    }

    private fun urlContainsTrackingKeyword(url: String): Boolean {
        val ampKeywords = trackingLinkDetectionRepository.ampKeywords

        ampKeywords.forEach { keyword ->
            if (url.contains(keyword)) {
                return true
            }
        }
        return false
    }

    fun extractCanonical(url: String): String? {
        val ampFormat = urlIsExtractableAmpLink(url) ?: return null
        val matchResult = ampFormat.find(url) ?: return null

        val groups = matchResult.groups
        if (groups.size < 2) return null

        var destinationUrl = groups[1]?.value ?: return null

        if (!destinationUrl.startsWith("http")) {
            destinationUrl = "https://$destinationUrl"
        }
        return destinationUrl
    }

    private fun urlIsExtractableAmpLink(url: String): Regex? {
        val ampLinkFormats = trackingLinkDetectionRepository.ampLinkFormats

        ampLinkFormats.forEach { format ->
            if (url.matches(format)) {
                return format
            }
        }
        return null
    }
}
