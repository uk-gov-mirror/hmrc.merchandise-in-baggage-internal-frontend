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

package uk.gov.hmrc.merchandiseinbaggage.support

import com.github.tomakehurst.wiremock.client.WireMock.{post, urlPathEqualTo, _}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.Json
import play.api.libs.json.Json.toJson
import uk.gov.hmrc.merchandiseinbaggage.config.MibConfiguration
import uk.gov.hmrc.merchandiseinbaggage.model.api.Declaration
import uk.gov.hmrc.merchandiseinbaggage.model.api.DeclarationId

object MibBackendStub extends MibConfiguration {

  val stubbedDeclarationId = DeclarationId("test-mib-be-id")

  def givenDeclarationIsPersistedInBackend(declaration: Declaration): StubMapping =
    stubFor(
      post(urlPathEqualTo(s"$declarationsUrl"))
        .withRequestBody(equalToJson(toJson(declaration).toString, true, false))
        .willReturn(aResponse().withStatus(201).withBody(Json.toJson(stubbedDeclarationId).toString)))

  def givenDeclarationIsPersistedInBackend(): StubMapping =
    stubFor(
      post(urlPathEqualTo(s"$declarationsUrl"))
        .willReturn(aResponse().withStatus(201).withBody(Json.toJson(stubbedDeclarationId).toString)))

  def givenPersistedDeclarationIsFound(declaration: Declaration, declarationId: DeclarationId = stubbedDeclarationId): StubMapping =
    stubFor(
      get(urlPathEqualTo(s"$declarationsUrl/${declarationId.value}"))
        .willReturn(okJson(Json.toJson(declaration).toString)))

  def givenSendEmailsSuccess(declarationId: DeclarationId = stubbedDeclarationId): StubMapping =
    stubFor(
      get(urlPathEqualTo(s"$sendEmailsUrl/${declarationId.value}"))
        .willReturn(aResponse().withStatus(202)))
}