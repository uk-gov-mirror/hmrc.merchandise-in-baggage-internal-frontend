/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.merchandiseinbaggage.stubs

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, post, urlPathEqualTo}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import uk.gov.hmrc.merchandiseinbaggage.CoreTestData

object StrideBackendStub extends CoreTestData {

  val authStr = s"""
                   |{
                   | "allEnrolments": [
                   |         {"key":"digital_tps_payment_taker_call_handler","identifiers":[{"key":"akey","value":"AA0497"}],"state":"Activated"},
                   |         {"key":"digital_mib_call_handler","identifiers":[{"key":"akey","value":"A1705A"}],"state":"Activated"}
                   |       ],
                   |  "optionalCredentials": {
                   |    "providerId": "strideId-1234456",
                   |    "providerType": "PrivilegedApplication"
                   |  }
                   |}""".stripMargin

  def givenStrideAuthorise()(implicit server: WireMockServer): StubMapping =
    server
      .stubFor(
        post(urlPathEqualTo("/auth/authorise"))
          .willReturn(aResponse().withStatus(200).withBody(authStr.stripMargin)))
}
