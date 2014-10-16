package infra.wscalacl

import org.specs2.mutable.Specification

/**
 * @author alari
 * @since 10/16/14
 */
class ApiUrlSpec extends Specification{

  val FbApiRoot = "https://graph.facebook.com/v2.1/"

  val MeApi = FbApiRoot + "me?fields=name,first_name,last_name,picture,email,verified,birthday,locale&return_ssl_resources=1&access_token="

  val MinFriends = 1

  val CheckingFriendsApi = FbApiRoot + s"me/friends?offset=${MinFriends - 1}&limit=1&return_ssl_resources=1&access_token="

  val FriendsApi = FbApiRoot + s"me/friends?limit=10000&return_ssl_resources=1&access_token="

  "api url" should {
    "build correct root" in {
      ApiUrl(FbApiRoot).toString must_== FbApiRoot
    }

    "build url with segment" in {
      (ApiUrl(FbApiRoot) / "me").toString must_== (FbApiRoot + "me")
      (ApiUrl(FbApiRoot) / 'node).toString must_== (FbApiRoot + ":node")
      (ApiUrl(FbApiRoot) / 'node).bind('node -> "me").toString must_== (FbApiRoot + "me")
    }

    "build query string" in {
      val api = ApiUrl(FbApiRoot)

      (api & ("a" -> "b")).toString must_== FbApiRoot + "?a=b"

      (api & 'token).bind('token -> "value").toString must_== FbApiRoot + "?token=value"

      (api & ("a" -> "b") & 't).bind('t -> "v").toString must_== FbApiRoot + "?a=b&t=v"
      (api & ("a" -> "b") & 't).toString must_== FbApiRoot + "?a=b"

      (api / 's & ("a" -> "b") & 't).bind('s -> "as").toString must_== FbApiRoot + "as?a=b"
    }
  }

}
