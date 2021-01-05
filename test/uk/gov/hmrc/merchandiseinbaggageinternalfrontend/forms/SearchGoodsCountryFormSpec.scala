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

package uk.gov.hmrc.merchandiseinbaggageinternalfrontend.forms

import play.api.data.FormError
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.forms.SearchGoodsCountryForm.form
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.forms.behaviours.FieldBehaviours
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.model.core.DeclarationType.{Export, Import}
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.support.BaseSpecWithApplication

class SearchGoodsCountryFormSpec extends BaseSpecWithApplication with FieldBehaviours {
  ".country" must {
    val fieldName = "country"
    val importRequiredKey = "searchGoodsCountry.error.Import.required"
    val exportRequiredKey = "searchGoodsCountry.error.Export.required"

    behave like mandatoryField(
      form(Import),
      fieldName,
      requiredError = FormError(fieldName, importRequiredKey)
    )

    behave like mandatoryField(
      form(Export),
      fieldName,
      requiredError = FormError(fieldName, exportRequiredKey)
    )
  }
}