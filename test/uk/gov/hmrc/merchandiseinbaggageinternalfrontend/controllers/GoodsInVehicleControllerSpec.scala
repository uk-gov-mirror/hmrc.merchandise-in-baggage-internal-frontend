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

package uk.gov.hmrc.merchandiseinbaggageinternalfrontend.controllers

import play.api.test.Helpers._
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.model.core._
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.support.MockStrideAuth.givenTheUserIsAuthenticatedAndAuthorised
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.support._
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.views.html.GoodsInVehicleView

import scala.concurrent.ExecutionContext.Implicits.global

class GoodsInVehicleControllerSpec extends DeclarationJourneyControllerSpec {

  val view = app.injector.instanceOf[GoodsInVehicleView]
  val controller: DeclarationJourney => GoodsInVehicleController =
    declarationJourney => new GoodsInVehicleController(component, stubProvider(declarationJourney), stubRepo(declarationJourney), view)

  forAll(declarationTypes) { importOrExport =>
    val journey: DeclarationJourney =
      DeclarationJourney(SessionId("123"), importOrExport, goodsEntries = GoodsEntries(Seq(completedGoodsEntry)))
    "onPageLoad" should {
      s"return 200 with radio buttons for $importOrExport" in {
        givenTheUserIsAuthenticatedAndAuthorised()

        val request = buildGet(routes.GoodsInVehicleController.onPageLoad().url)
        val eventualResult = controller(givenADeclarationJourneyIsPersisted(journey)).onPageLoad()(request)

        status(eventualResult) mustBe 200
        contentAsString(eventualResult) must include(messages(s"goodsInVehicle.$importOrExport.title"))
        contentAsString(eventualResult) must include(messages(s"goodsInVehicle.$importOrExport.heading"))
      }
    }

    "onSubmit" should {
      forAll(goodsInVehicleAnswer) { (yesOrNo, redirectTo) =>
        s"redirect to $redirectTo after successful form submit with $yesOrNo for $importOrExport" in {
          givenTheUserIsAuthenticatedAndAuthorised()

          val request = buildGet(routes.GoodsInVehicleController.onSubmit().url)
            .withFormUrlEncodedBody("value" -> yesOrNo.entryName)

          val eventualResult = controller(givenADeclarationJourneyIsPersisted(journey)).onSubmit()(request)

          status(eventualResult) mustBe 303
          redirectLocation(eventualResult).get must endWith(redirectTo)
        }
      }

      s"return 400 with any form errors for $importOrExport" in {
        givenTheUserIsAuthenticatedAndAuthorised()

        val request = buildGet(routes.GoodsInVehicleController.onSubmit().url)
          .withFormUrlEncodedBody("value" -> "in valid")

        val eventualResult = controller(givenADeclarationJourneyIsPersisted(journey)).onSubmit()(request)
        val result = contentAsString(eventualResult)

        status(eventualResult) mustBe 400
        result must include(messageApi("error.summary.title"))
        result must include(messages(s"goodsInVehicle.$importOrExport.title"))
        result must include(messages(s"goodsInVehicle.$importOrExport.heading"))
      }
    }
  }
}
