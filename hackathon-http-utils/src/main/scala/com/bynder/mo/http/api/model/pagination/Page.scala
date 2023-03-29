package com.bynder.mo.http.api.model.pagination

case class Page[T](pageInfo: PageInfo, data: Seq[T])
