/*
 * Copyright 2020 HM Revenue & Customs
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

import play.api.mvc.AnyContentAsEmpty
import play.api.test.CSRFTokenHelper._
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, _}
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.model.core.DeclarationType.Export
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.support.CurrencyConversionSupport.givenSuccessfulCurrencyConversionResponse
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.support.MibBackendStub.givenDeclarationIsPersistedInBackend
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.support.MockStrideAuth.givenTheUserIsAuthenticatedAndAuthorised
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.support.PayApiStub._
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.support._
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.views.html.{CheckYourAnswersExportView, CheckYourAnswersImportView}

import scala.concurrent.ExecutionContext.Implicits.global

class CheckYourAnswersControllerSpec extends BaseSpecWithApplication {

  val importView = app.injector.instanceOf[CheckYourAnswersImportView]
  val exportView = app.injector.instanceOf[CheckYourAnswersExportView]
  val controller = new CheckYourAnswersController(
    component,
    actionProvider,
    calculationService,
    paymentConnector,
    mibConnector,
    repo,
    importView,
    exportView)

  "onPageLoad" should {
    "return 200 with radio buttons" in {
      givenTheUserIsAuthenticatedAndAuthorised()
      givenADeclarationJourneyIsPersisted(completedDeclarationJourney)
      givenSuccessfulCurrencyConversionResponse()

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad().url)
        .withSession((SessionKeys.sessionId, sessionId.value))
        .withCSRFToken
        .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

      val eventualResult = controller.onPageLoad()(request)
      status(eventualResult) mustBe 200
      contentAsString(eventualResult) must include(messages("checkYourAnswers.title"))
      contentAsString(eventualResult) must include(messages("checkYourAnswers.change"))
      contentAsString(eventualResult) must include(messages("checkYourAnswers.detailsOfTheGoods"))
      contentAsString(eventualResult) must include(messages("checkYourAnswers.detailsOfTheGoods.category"))
      contentAsString(eventualResult) must include(messages("checkYourAnswers.detailsOfTheGoods.quantity"))
      contentAsString(eventualResult) must include(messages("checkYourAnswers.detailsOfTheGoods.vatRate"))
      contentAsString(eventualResult) must include(messages("checkYourAnswers.detailsOfTheGoods.country"))
      contentAsString(eventualResult) must include(messages("checkYourAnswers.detailsOfTheGoods.price"))
      contentAsString(eventualResult) must include(messages("checkYourAnswers.detailsOfTheGoods.paymentDue"))
      contentAsString(eventualResult) must include(messages("checkYourAnswers.addMoreGoods"))
      contentAsString(eventualResult) must include(messages("checkYourAnswers.personalDetails"))
      //TODO: Add others
    }
  }

  "onSubmit" should {
    "redirect to payment page after successful form submit for Imports" in {
      givenTheUserIsAuthenticatedAndAuthorised()
      givenSuccessfulCurrencyConversionResponse()
      givenADeclarationJourneyIsPersisted(completedDeclarationJourney)
      givenDeclarationIsPersistedInBackend()
      givenTaxArePaid()

      val request = FakeRequest(POST, routes.CheckYourAnswersController.onSubmit().url)
        .withSession((SessionKeys.sessionId, sessionId.value))
        .withCSRFToken
        .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

      val eventualResult = controller.onSubmit()(request)
      status(eventualResult) mustBe 303
      redirectLocation(eventualResult) mustBe Some("http://localhost:17777/pay/initiate-journey") //TODO: fix this test
    }

    "redirect to payment page after successful form submit for Exports" in {
      givenTheUserIsAuthenticatedAndAuthorised()
      givenSuccessfulCurrencyConversionResponse()
      givenADeclarationJourneyIsPersisted(completedDeclarationJourney.copy(declarationType = Export))
      givenDeclarationIsPersistedInBackend()

      val request = FakeRequest(POST, routes.CheckYourAnswersController.onSubmit().url)
        .withSession((SessionKeys.sessionId, sessionId.value))
        .withCSRFToken
        .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

      val eventualResult = controller.onSubmit()(request)
      status(eventualResult) mustBe 303
      redirectLocation(eventualResult) mustBe Some(routes.DeclarationConfirmationController.onPageLoad().url)
    }
  }
}
