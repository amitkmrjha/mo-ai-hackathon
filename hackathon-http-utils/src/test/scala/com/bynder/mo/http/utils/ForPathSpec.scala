package com.bynder.mo.http.utils

import akka.http.scaladsl.model.Uri
import com.bynder.mo.http.client.ServerToken
import org.scalatest.flatspec.AsyncFlatSpec

class ForPathSpec extends AsyncFlatSpec:
  "Uri.forPath" should "return a uri with port" in {
    given token: ServerToken = new { val host = "localhost"; val port = Some(8443); val token = "bearer token" }
    val uri                  = Uri.forPath("/path", None)
    assert(uri.toString() == "https://localhost:8443/path")
  }

  it should "return a uri with no port" in {
    given token: ServerToken = new { val host = "localhost"; val port = None; val token = "bearer token" }
    val uri                  = Uri.forPath("/path", None)
    assert(uri.toString() == "https://localhost/path")
  }

  it should "return a uri with query" in {
    given token: ServerToken = new { val host = "localhost"; val port = None; val token = "bearer token" }
    val uri                  = Uri.forPath("/path", Some("v1=1&v2=2"))
    assert(uri.toString() == "https://localhost/path?v1=1&v2=2")
  }
