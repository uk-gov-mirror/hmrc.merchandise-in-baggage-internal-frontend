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

package uk.gov.hmrc.merchandiseinbaggage.smoketests

import uk.gov.hmrc.merchandiseinbaggage.model.core.ImportExportChoices.AddToExisting
import uk.gov.hmrc.merchandiseinbaggage.model.core.RetrieveDeclaration
import uk.gov.hmrc.merchandiseinbaggage.smoketests.pages._
import uk.gov.hmrc.merchandiseinbaggage.support.MockStrideAuth

class AmendJourneySpec extends BaseUiSpec {

  "AddToExisting journey - happy path" should {
    "work as expected" in {
      goto(ImportExportChoicePage.path)

      submitPage(ImportExportChoicePage, AddToExisting.toString)

      submitPage(RetrieveDeclarationPage, RetrieveDeclaration(mibReference, eori))
    }
  }
}
