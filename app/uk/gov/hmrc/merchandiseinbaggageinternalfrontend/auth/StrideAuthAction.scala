/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.merchandiseinbaggageinternalfrontend.auth

import javax.inject.Inject
import org.slf4j.LoggerFactory.getLogger
import play.api.mvc.Results.Forbidden
import play.api.mvc._
import play.api.{Configuration, Environment}
import uk.gov.hmrc.auth.core.AuthProvider.PrivilegedApplication
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.Credentials
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.credentials
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.merchandiseinbaggageinternalfrontend.config.AppConfig
import uk.gov.hmrc.play.HeaderCarrierConverter
import uk.gov.hmrc.play.bootstrap.config.AuthRedirects

import scala.concurrent.{ExecutionContext, Future}

class StrideAuthAction @Inject()(override val authConnector: AuthConnector,
                                 appConfig: AppConfig,
                                 mcc: MessagesControllerComponents
                                )(implicit ec: ExecutionContext)
  extends ActionBuilder[AuthRequest, AnyContent]
    with AuthorisedFunctions
    with AuthRedirects {

  override def parser: BodyParser[AnyContent] = mcc.parsers.defaultBodyParser

  override def config: Configuration = appConfig.config

  override def env: Environment = appConfig.env

  override protected def executionContext: ExecutionContext = ec

  private val logger = getLogger(getClass)

  override def invokeBlock[A](request: Request[A], block: AuthRequest[A] => Future[Result]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    val strideEnrolment = Enrolment(appConfig.strideRole)

    def redirectToStrideLogin(message: String) = {
      logger.warn(s"user is not authenticated - redirecting user to login: $message")
      val uri = if (request.host.contains("localhost")) s"http://${request.host}${request.uri}" else s"${request.uri}"
      toStrideLogin(uri)
    }

    authorised(strideEnrolment and AuthProviders(PrivilegedApplication)).retrieve(credentials) {
      case Some(c: Credentials) => block(new AuthRequest(request, c))
      case None =>
        Future successful redirectToStrideLogin("User does not have credentials")
    }.recover {
      case e: NoActiveSession =>
        redirectToStrideLogin(e.getMessage)
      case e: InternalError =>
        redirectToStrideLogin(e.getMessage)
      case e: AuthorisationException =>
        logger.warn(s"User is forbidden because of ${e.reason}, $e")
        Forbidden
    }
  }
}