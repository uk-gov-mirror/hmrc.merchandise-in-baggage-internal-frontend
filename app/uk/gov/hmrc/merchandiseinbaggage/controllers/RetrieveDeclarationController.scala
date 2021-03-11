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

import cats.implicits._
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.merchandiseinbaggage.config.{AmendDeclarationConfiguration, AppConfig}
import uk.gov.hmrc.merchandiseinbaggage.connectors.MibConnector
import uk.gov.hmrc.merchandiseinbaggage.forms.RetrieveDeclarationForm.form
import uk.gov.hmrc.merchandiseinbaggage.model.core.RetrieveDeclaration
import uk.gov.hmrc.merchandiseinbaggage.repositories.DeclarationJourneyRepository
import uk.gov.hmrc.merchandiseinbaggage.utils.Utils.FutureOps
import uk.gov.hmrc.merchandiseinbaggage.views.html.RetrieveDeclarationView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveDeclarationController @Inject()(
  override val controllerComponents: MessagesControllerComponents,
  actionProvider: DeclarationJourneyActionProvider,
  override val repo: DeclarationJourneyRepository,
  mibConnector: MibConnector,
  view: RetrieveDeclarationView
)(implicit appConfig: AppConfig, val ec: ExecutionContext)
    extends DeclarationJourneyUpdateController with AmendDeclarationConfiguration {

  override val onPageLoad: Action[AnyContent] = actionProvider.journeyAction { implicit request =>
    if (amendFlagConf.canBeAmended) {
      // TODO restore link, using InvalidRequestController until NewOrExistingController working again
      Ok(view(form, routes.InvalidRequestController.onPageLoad(), request.declarationJourney.declarationType))
      //     Ok(view(form, routes.NewOrExistingController.onPageLoad(), request.declarationJourney.declarationType))
    } else Redirect(routes.InvalidRequestController.onPageLoad().url)
//    } else Redirect(routes.CannotAccessPageController.onPageLoad().url )
  }

  override val onSubmit: Action[AnyContent] = actionProvider.journeyAction.async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors =>
          // TODO restore link, using InvalidRequestController until NewOrExistingController working again
          BadRequest(view(formWithErrors, routes.InvalidRequestController.onPageLoad(), request.declarationJourney.declarationType)).asFuture,
//          BadRequest(view(formWithErrors, routes.NewOrExistingController.onPageLoad(), request.declarationJourney.declarationType)).asFuture,
        validData => processRequest(validData)
      )
  }

  private def processRequest(
    validData: RetrieveDeclaration)(implicit request: DeclarationJourneyRequest[AnyContent], hc: HeaderCarrier, ec: ExecutionContext) =
    mibConnector
      .findBy(validData.mibReference, validData.eori)
      .fold(
        error => Future successful InternalServerError(error), {
          case Some(id) =>
            repo.upsert(request.declarationJourney.copy(declarationId = id)) map { _ =>
              Redirect(routes.PreviousDeclarationDetailsController.onPageLoad())
            }
          // TODO url links
          case None => Future successful Redirect(routes.InvalidRequestController.onPageLoad())
//          case None => Future successful Redirect(routes.DeclarationNotFoundController.onPageLoad() )
        }
      )
      .flatten
}
