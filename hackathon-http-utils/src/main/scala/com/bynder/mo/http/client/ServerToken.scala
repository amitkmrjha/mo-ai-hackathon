package com.bynder.mo.http.client

trait ServerToken {
  def scheme: String = "https"
  def host: String
  def token: String
  def port: Option[Int]
}
