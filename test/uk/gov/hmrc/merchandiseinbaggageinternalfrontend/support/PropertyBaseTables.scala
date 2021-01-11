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

package uk.gov.hmrc.merchandiseinbaggageinternalfrontend.support

import org.scalatest.prop.{TableFor1, TableFor2}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.controllers.routes
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.model.core.DeclarationType.{Export, Import}
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.model.core.GoodsDestinations.{GreatBritain, NorthernIreland}
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.model.core.YesNo.{No, Yes}
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.model.core.{DeclarationType, GoodsDestination, YesNo}

trait PropertyBaseTables extends ScalaCheckPropertyChecks {

  val declarationTypes: TableFor1[DeclarationType] = Table("declarationType", Import, Export)

  val traderYesOrNoAnswer = Table(
    ("answer", "trader or agent"),
    (Yes, "agent"),
    (No, "trader")
  )

  val customAgentYesOrNoAnswer: TableFor2[YesNo, String] = Table(
    ("radio button yes/no", "redirectTo"),
    (Yes, routes.AgentDetailsController.onPageLoad().url),
    (No, routes.EoriNumberController.onPageLoad().url)
  )

  val goodsDestinationAnswer: TableFor2[GoodsDestination, String] = Table(
    ("radio button NorthernIreland/GreatBritain", "redirectTo"),
    (GreatBritain, routes.ExciseAndRestrictedGoodsController.onPageLoad().url),
    (NorthernIreland, routes.CannotUseServiceIrelandController.onPageLoad().url)
  )

  val exciseAndRestrictedGoodsYesOrNoAnswer: TableFor2[YesNo, String] = Table(
    ("radio button yes/no", "redirectTo"),
    (Yes, routes.CannotUseServiceController.onPageLoad().url),
    (No, routes.ValueWeightOfGoodsController.onPageLoad().url)
  )

  val goodsInVehicleAnswer: TableFor2[YesNo, String] = Table(
    ("radio button yes/no", "redirectTo"),
    (Yes, routes.VehicleSizeController.onPageLoad().url),
    (No, routes.CheckYourAnswersController.onPageLoad().url)
  )

  val paymentCalculationThreshold: TableFor2[String, String] = Table(
    ("threshold", "redirectTo"),
    ("150001", routes.GoodsOverThresholdController.onPageLoad().url),
    ("99.99", routes.CustomsAgentController.onPageLoad().url)
  )
}
