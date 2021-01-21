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

package uk.gov.hmrc.merchandiseinbaggage.controllers

import play.api.test.Helpers._
import uk.gov.hmrc.merchandiseinbaggage.model.api.SessionId
import uk.gov.hmrc.merchandiseinbaggage.model.core.DeclarationJourney
import uk.gov.hmrc.merchandiseinbaggage.support.MockStrideAuth.givenTheUserIsAuthenticatedAndAuthorised
import uk.gov.hmrc.merchandiseinbaggage.support._
import uk.gov.hmrc.merchandiseinbaggage.views.html.GoodsTypeQuantityView

import scala.concurrent.ExecutionContext.Implicits.global

class GoodsTypeQuantityControllerSpec extends DeclarationJourneyControllerSpec {

  val view = app.injector.instanceOf[GoodsTypeQuantityView]
  val controller: DeclarationJourney => GoodsTypeQuantityController =
    declarationJourney => new GoodsTypeQuantityController(component, stubProvider(declarationJourney), stubRepo(declarationJourney), view)

  forAll(declarationTypes) { importOrExport =>
    val journey: DeclarationJourney = DeclarationJourney(SessionId("123"), importOrExport)
    "onPageLoad" should {
      s"return 200 with radio buttons for $importOrExport" in {
        givenTheUserIsAuthenticatedAndAuthorised()

        val request = buildGet(routes.GoodsTypeQuantityController.onPageLoad(1).url)
        val eventualResult = controller(givenADeclarationJourneyIsPersisted(journey)).onPageLoad(1)(request)
        val result = contentAsString(eventualResult)

        status(eventualResult) mustBe 200
        result must include(messageApi(s"goodsTypeQuantity.$importOrExport.title"))
        result must include(messageApi(s"goodsTypeQuantity.$importOrExport.heading"))
        result must include(messageApi("goodsTypeQuantity.p"))
        result must include(messageApi("goodsTypeQuantity.quantity"))
      }
    }

    "onSubmit" should {
      s"redirect to next page after successful form submit for $importOrExport" in {
        givenTheUserIsAuthenticatedAndAuthorised()
        val request = buildGet(routes.GoodsTypeQuantityController.onSubmit(1).url)
          .withFormUrlEncodedBody("category" -> "clothes", "quantity" -> "1")

        val eventualResult = controller(givenADeclarationJourneyIsPersisted(journey)).onSubmit(1)(request)
        status(eventualResult) mustBe 303
        redirectLocation(eventualResult) mustBe Some(routes.GoodsVatRateController.onPageLoad(1).url)
      }

      s"return 400 with any form errors for $importOrExport" in {
        givenTheUserIsAuthenticatedAndAuthorised()
        val request = buildGet(routes.GoodsTypeQuantityController.onSubmit(1).url)
          .withFormUrlEncodedBody("xyz" -> "clothes", "abc" -> "1")

        val eventualResult = controller(givenADeclarationJourneyIsPersisted(journey)).onSubmit(1)(request)
        val result = contentAsString(eventualResult)

        status(eventualResult) mustBe 400
        result must include(messageApi("error.summary.title"))
        result must include(messageApi(s"goodsTypeQuantity.$importOrExport.title"))
        result must include(messageApi(s"goodsTypeQuantity.$importOrExport.heading"))
        result must include(messageApi("goodsTypeQuantity.p"))
        result must include(messageApi("goodsTypeQuantity.quantity"))
      }
    }
  }
}