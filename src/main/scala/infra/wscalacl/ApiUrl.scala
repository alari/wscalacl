package infra.wscalacl

import java.net.URLEncoder

/**
 * @author alari
 * @since 10/16/14
 */
case class ApiUrl(
                   root: String,
                   segments: Seq[Either[Symbol,String]] = Seq.empty,
                   query: Seq[(String,String)] = Seq.empty,
                   queryQ: Set[Symbol] = Set.empty
                   ) {

  /**
   * Adds a literal segment to url
   * @param segment url segment
   * @return
   */
  def /(segment: String) = copy(segments = segments :+ Right(segment))

  /**
   * Adds a token segment to url. You can bind to this token with .apply
   * @param token symbol
   * @return
   */
  def /(token: Symbol) = {
    require(!params.contains(token), "This token has already been used")
    copy(segments = segments :+ Left(token))
  }

  /**
   * Returns built URL with query string. For unbound url segments, uses its symbols prepared with :
   * @return
   */
  override def toString = root + segments.map {
    case Left(s) => ":" + s.name
    case Right(s) => s
  }.mkString("/") + (query match {
    case Seq() => ""
    case sq =>
      "?" + sq.map(kv => URLEncoder.encode(kv._1, "UTF-8") + "=" + URLEncoder.encode(kv._2, "UTF-8")).mkString("&")
  })

  /**
   * Returns unbound params, both for query string and url segments
   * @return
   */
  lazy val params: Set[Symbol] = queryQ ++ segments.collect {
    case Left(s) => s
  }

  /**
   * Binds a value to an url segment or a query parameter
   * @param bs segments and values to bind
   * @return
   */
  def bind(bs: (Symbol, Any)*) = apply(bs:_*)

  /**
   * Binds values to url segments or query parameters
   * @param bs segments and values to bind
   * @return
   */
  def apply(bs: (Symbol, Any)*) = {
    val bsMap = bs.toMap

    val ps = params

    require(!bsMap.keys.exists(k => !ps.contains(k)), "Cannot bind unused token")

    val querySegments = bsMap.keys.filter(queryQ.contains)

    val queryQUp = queryQ -- querySegments

    val queryParams = query ++ querySegments.map {
      s =>
        s.name -> bsMap(s).toString
    }
    copy(segments = segments.map {
      case Left(s) if bsMap.contains(s) => Right(bsMap(s).toString)
      case s => s
    }, queryQ = queryQUp, query = queryParams)
  }

  /**
   * Adds a query parameter to url
   * @param queryParam key-value
   * @return
   */
  def &(queryParam: (String, Any)) = copy(query = query :+ queryParam.copy(_2 = queryParam._2.toString))

  /**
   * Adds a query parameter to be bound (optional)
   * @param token symbol to be bound later
   * @return
   */
  def &(token: Symbol) = {
    require(!params.contains(token), "This token has already been used")
    copy(queryQ = queryQ + token)
  }

  /**
   * Returns true if url is complete and can be called (e.g. all url segments are filled with values)
   * @return
   */
  def isComplete: Boolean = !segments.exists(_.isLeft)
}
