package infra.wscalacl

import org.specs2.mutable.Specification

import scala.util.{Success, Failure}

/**
 * @author alari
 * @since 10/16/14
 */
class ApiPostProcessSpec extends Specification {

  "post processor" should {


    type Resp = String

    trait Codomain
    case class OkResp(num: Int) extends Codomain
    case class ErrResp(reason: String) extends Throwable with Codomain

    val errR = ApiPostProcess[String,Codomain]((s: String) => s match {
      case "err" => Some(ErrResp("it's an error"))
      case _ => None
    })

    "parse error" in {
      errR.parseError("err") must_== Some(ErrResp("it's an error"))
      errR.parseError("some") must beNone
    }

    "produce post processor" in {

      val allR = errR((s: String) => OkResp(1))

      allR("err") must_== Failure(ErrResp("it's an error"))
      allR("some") must_== Success(OkResp(1))

    }

  }

}
