package infra.wscalacl

import scala.concurrent.Future
import scala.util.{Success, Failure, Try}

/**
 * @author alari
 * @since 10/16/14
 *
 *
 * Wraps error and success parsers for API post-processing
 * @param parseError response reader for errors
 * @param parseSuccess response reader for successful response
 * @tparam R response type
 * @tparam C codomain
 */
case class ApiPostProcess[R,C](parseError: R => Option[Throwable], parseSuccess: R => C) extends (R => Try[C]) {
  /**
   * Actually process response (either error or success)
   * @param resp processed response
   * @return
   */
  def apply(resp: R): Try[C] = for {
    er <- Try(parseError(resp))
    res <- er match {
      case Some(e) => Failure(e)
      case None => Try(parseSuccess(resp))
    }
  } yield res

  /**
   * Actually read response and return it as a future
   * @param resp processed response
   * @return
   */
  def applyM(resp: R): Future[C] = apply(resp) match {
    case Success(r) => Future.successful(r)
    case Failure(e) => Future.failed(e)
  }
}

object ApiPostProcess {
  case class ParseError[R](parseError: R => Option[Throwable]) {
    def apply[ C](parseSuccess: R => C) = ApiPostProcess(parseError, parseSuccess)
  }

  /**
   * Wraps error parser, to build complete postprocessor for concrete response later
   * @param parseError response reader for errors
   * @tparam R response type
   * @tparam C API codomain
   * @return
   */
  def apply[R, C](parseError: R => Option[Throwable]) = ParseError(parseError)
}