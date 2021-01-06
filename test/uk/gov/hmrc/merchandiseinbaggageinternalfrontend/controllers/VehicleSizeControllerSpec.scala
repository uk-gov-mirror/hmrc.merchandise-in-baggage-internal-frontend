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
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.views.html.VehicleSizeView

import scala.concurrent.ExecutionContext.Implicits.global

class VehicleSizeControllerSpec extends DeclarationJourneyControllerSpec {

  val view = app.injector.instanceOf[VehicleSizeView]
  val controller: DeclarationJourney => VehicleSizeController =
    declarationJourney => new VehicleSizeController(component, stubProvider(declarationJourney), stubRepo(declarationJourney), view)

  private val journey: DeclarationJourney = DeclarationJourney(
    SessionId("123"),
    DeclarationType.Import,
    goodsEntries = GoodsEntries(Seq(completedGoodsEntry))
  )

  "onPageLoad" should {
    "return 200 with radio buttons" in {
      givenTheUserIsAuthenticatedAndAuthorised()

      val request = buildGet(routes.VehicleSizeController.onPageLoad().url)
      val eventualResult = controller(givenADeclarationJourneyIsPersisted(journey)).onPageLoad()(request)
      val result = contentAsString(eventualResult)

      status(eventualResult) mustBe 200
      result must include(messages("vehicleSize.Import.title"))
      result must include(messages("vehicleSize.Import.heading"))
      result must include(messages("vehicleSize.hint"))
    }
  }

  "onSubmit" should {
    "redirect to /vehicle-reg-no after successful form submit with Yes" in {
      givenTheUserIsAuthenticatedAndAuthorised()

      val request = buildGet(routes.VehicleSizeController.onSubmit().url)
        .withFormUrlEncodedBody("value" -> "Yes")
      val eventualResult = controller(givenADeclarationJourneyIsPersisted(journey)).onSubmit()(request)

      status(eventualResult) mustBe 303
      redirectLocation(eventualResult) mustBe Some(routes.VehicleRegistrationNumberController.onPageLoad().url)
    }

    "redirect to /cannot-use-service after successful form submit with No" in {
      givenTheUserIsAuthenticatedAndAuthorised()
      val declarationJourney = DeclarationJourney(
        SessionId("123"),
        DeclarationType.Import,
        goodsEntries = GoodsEntries(Seq(completedGoodsEntry, completedGoodsEntry))
      )

      val request = buildGet(routes.VehicleSizeController.onSubmit().url)
        .withFormUrlEncodedBody("value" -> "No")
      val eventualResult = controller(givenADeclarationJourneyIsPersisted(declarationJourney)).onSubmit()(request)

      status(eventualResult) mustBe 303
      redirectLocation(eventualResult) mustBe Some(routes.CannotUseServiceController.onPageLoad().url)
    }

    "return 400 with any form errors" in {
      givenTheUserIsAuthenticatedAndAuthorised()

      val request = buildGet(routes.VehicleSizeController.onSubmit().url)
        .withFormUrlEncodedBody("value" -> "in valid")

      val eventualResult = controller(givenADeclarationJourneyIsPersisted(journey)).onSubmit()(request)
      val result = contentAsString(eventualResult)

      status(eventualResult) mustBe 400
      result must include(messageApi("error.summary.title"))
      result must include(messages("vehicleSize.Import.title"))
      result must include(messages("vehicleSize.Import.heading"))
      result must include(messages("vehicleSize.hint"))
    }
  }
}
