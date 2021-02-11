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

package uk.gov.hmrc.merchandiseinbaggage.service

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.merchandiseinbaggage.CoreTestData
import uk.gov.hmrc.merchandiseinbaggage.model.api.AmountInPence
import uk.gov.hmrc.merchandiseinbaggage.model.api.calculation.{CalculationResult, CalculationResults}
import uk.gov.hmrc.merchandiseinbaggage.support.BaseSpecWithApplication
import uk.gov.hmrc.merchandiseinbaggage.support.MibBackendStub._
import uk.gov.hmrc.merchandiseinbaggage.utils.DataModelEnriched._

class CalculationServiceSpec extends BaseSpecWithApplication with CoreTestData {

  private implicit val hc: HeaderCarrier = HeaderCarrier()

  private val service = injector.instanceOf[CalculationService]

  "retrieve payment calculations from mib backend" in {
    val stubbedResult =
      CalculationResult(aImportGoods, AmountInPence(7835), AmountInPence(0), AmountInPence(1567), Some(aConversionRatePeriod))
    val expected = CalculationResults(List(stubbedResult))

    givenAPaymentCalculations(List(stubbedResult.goods).map(_.calculationRequest), List(stubbedResult))

    service.paymentCalculations(Seq(aImportGoods)).futureValue mustBe expected
  }
}