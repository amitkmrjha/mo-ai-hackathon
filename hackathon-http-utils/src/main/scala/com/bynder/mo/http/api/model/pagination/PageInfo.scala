package com.bynder.mo.http.api.model.pagination

case class PageInfo(
    start: Int = 0,
    limit: Int = PageInfo.DEFAULT_PAGE_SIZE,
    total: Option[Int] = None,
    isFinal: Boolean = false
) {
  def next: Option[PageInfo] =
    if (start < 0) then None
    else if isFinal then None
    else if total.isDefined && start + limit > total.get then None
    else Option(PageInfo(start + limit, limit, total, isFinal))
}

object PageInfo {
  def DEFAULT_PAGE_SIZE = 50
}
