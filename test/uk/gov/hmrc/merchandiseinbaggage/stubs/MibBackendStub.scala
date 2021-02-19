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
import com.github.tomakehurst.wiremock.client.WireMock.{post, urlPathEqualTo, _}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.Json
import play.api.libs.json.Json.toJson
import uk.gov.hmrc.merchandiseinbaggage.CoreTestData
import uk.gov.hmrc.merchandiseinbaggage.config.MibConfiguration
import uk.gov.hmrc.merchandiseinbaggage.model.api.DeclarationType.Export
import uk.gov.hmrc.merchandiseinbaggage.model.api.{Declaration, DeclarationId}
import uk.gov.hmrc.merchandiseinbaggage.model.api.calculation.{CalculationRequest, CalculationResult}

object MibBackendStub extends MibConfiguration with CoreTestData {

  val stubbedDeclarationId = DeclarationId("test-mib-be-id")

  def givenDeclarationIsPersistedInBackend(declaration: Declaration)(implicit server: WireMockServer): StubMapping =
    server
      .stubFor(
        post(urlPathEqualTo(s"$declarationsUrl"))
          .withRequestBody(equalToJson(toJson(declaration).toString, true, false))
          .willReturn(aResponse().withStatus(201).withBody(Json.toJson(stubbedDeclarationId).toString)))

  def givenDeclarationIsPersistedInBackend()(implicit server: WireMockServer): StubMapping =
    server
      .stubFor(
        post(urlPathEqualTo(s"$declarationsUrl"))
          .willReturn(aResponse().withStatus(201).withBody(Json.toJson(stubbedDeclarationId).toString)))

  def givenPersistedDeclarationIsFound(declaration: Declaration, declarationId: DeclarationId = stubbedDeclarationId)(
    implicit server: WireMockServer): StubMapping =
    server
      .stubFor(
        get(urlPathEqualTo(s"$declarationsUrl/${declarationId.value}"))
          .willReturn(okJson(Json.toJson(declaration).toString)))

  def givenPersistedDeclarationIsFound()(implicit server: WireMockServer): StubMapping =
    server
      .stubFor(
        get(urlMatching(s"$declarationsUrl/(.*)"))
          .willReturn(okJson(Json.toJson(declaration.copy(declarationId = stubbedDeclarationId, declarationType = Export)).toString)))

  def givenAPaymentCalculations(requests: Seq[CalculationRequest], results: Seq[CalculationResult])(
    implicit server: WireMockServer): StubMapping =
    server
      .stubFor(
        post(urlPathEqualTo(s"$calculationsUrl"))
          .withRequestBody(equalToJson(toJson(requests).toString, true, false))
          .willReturn(okJson(Json.toJson(results).toString)))

  def givenAPaymentCalculation(results: Seq[CalculationResult])(implicit server: WireMockServer): StubMapping =
    server
      .stubFor(
        post(urlPathEqualTo(s"$calculationsUrl"))
          .willReturn(okJson(Json.toJson(results).toString)))

  def givenEoriIsChecked(eoriNumber: String)(implicit server: WireMockServer): StubMapping =
    server
      .stubFor(
        get(urlPathEqualTo(s"$checkEoriUrl$eoriNumber"))
          .willReturn(ok().withBody(Json.toJson(aCheckResponse).toString)))
}