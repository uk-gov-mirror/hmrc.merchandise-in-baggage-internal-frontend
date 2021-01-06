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

import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.model.core.{DeclarationJourney, DeclarationType, SessionId, YesNo}
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.support.MockStrideAuth.givenTheUserIsAuthenticatedAndAuthorised
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.support._
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.views.html.EoriNumberView

import scala.concurrent.ExecutionContext.Implicits.global

class EoriNumberControllerSpec extends DeclarationJourneyControllerSpec {

  val view = app.injector.instanceOf[EoriNumberView]
  val controller: DeclarationJourney => EoriNumberController =
    declarationJourney => new EoriNumberController(component, stubProvider(declarationJourney), stubRepo(declarationJourney), view)

  private val journey: DeclarationJourney =
    DeclarationJourney(SessionId("123"), DeclarationType.Import, maybeIsACustomsAgent = Some(YesNo.No))

  "onPageLoad" should {
    "return 200 with radio buttons" in {
      givenTheUserIsAuthenticatedAndAuthorised()

      val request = buildGet(routes.EoriNumberController.onPageLoad.url)
      val eventualResult = controller(givenADeclarationJourneyIsPersisted(journey)).onPageLoad(request)
      val result = contentAsString(eventualResult)

      status(eventualResult) mustBe 200
      result must include(messageApi("eoriNumber.trader.Import.title"))
      result must include(messageApi("eoriNumber.trader.Import.heading"))
      result must include(messageApi("eoriNumber.hint"))
    }
  }

  "onSubmit" should {
    "redirect to next page after successful form submit" in {
      givenTheUserIsAuthenticatedAndAuthorised()
      val request = buildGet(routes.EoriNumberController.onSubmit().url)
        .withFormUrlEncodedBody("eori" -> "GB123456780000")

      val eventualResult = controller(givenADeclarationJourneyIsPersisted(journey)).onSubmit(request)

      status(eventualResult) mustBe 303
      redirectLocation(eventualResult) mustBe Some(routes.TravellerDetailsController.onPageLoad().url)
    }

    "return 400 with any form errors" in {
      givenTheUserIsAuthenticatedAndAuthorised()
      val request = buildGet(routes.EoriNumberController.onSubmit().url)
        .withFormUrlEncodedBody("eori" -> "in valid")

      val eventualResult = controller(givenADeclarationJourneyIsPersisted(journey)).onSubmit()(request)
      val result = contentAsString(eventualResult)

      status(eventualResult) mustBe 400
      result must include(messageApi("eoriNumber.trader.Import.title"))
      result must include(messageApi("eoriNumber.trader.Import.heading"))
      result must include(messageApi("eoriNumber.hint"))
    }
  }
}
