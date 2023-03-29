package com.bynder.mo.http.api

import com.github.swagger.akka.SwaggerHttpService
import com.github.swagger.akka.model.Info
import com.typesafe.config.ConfigFactory
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.{SecurityRequirement, SecurityScheme}
import io.swagger.v3.oas.models.servers.{Server, ServerVariable, ServerVariables}

import scala.jdk.CollectionConverters.*

trait SwaggerDocServiceBase extends SwaggerHttpService:
  val BEARER_AUTH_SCHEME           = "bearer-jwt";
  override val unwantedDefinitions = Seq("Function1", "Function1RequestContextFutureRouteResult")
  override val security            = List(new SecurityRequirement().addList(BEARER_AUTH_SCHEME))
  val hostConfigKey: String
  val portConfigKey: String

  override def swaggerConfig: OpenAPI =
    val config = super.swaggerConfig

    lazy val appConfig = ConfigFactory.load()

    lazy val host = appConfig.getString(hostConfigKey)
    lazy val port = appConfig.getInt(portConfigKey)

    val parameterizedServer = new Server()
    parameterizedServer.description("Bynder Portal")
    parameterizedServer.url("{scheme}://{portalHostname}{prefix}")

    val serverVariables = new ServerVariables()
    serverVariables.put(
      "portalHostname",
      new ServerVariable()
        ._default("www.bynder-stage.com/v7")
        .description("The portal to make the request to")
    )

    serverVariables.put(
      "scheme",
      new ServerVariable()
        ._enum(List("http", "https").asJava)
        ._default("https")
    )

    serverVariables.put(
      "prefix",
      new ServerVariable()
        ._enum(List("/").asJava)
        ._default("/")
    )

    val localDevServer = new Server()
    localDevServer.description("Local Development")
    localDevServer.url(s"http://$host:$port")

    parameterizedServer.variables(serverVariables)

    config.servers(List(parameterizedServer, localDevServer).asJava)

    config

  override def securitySchemes: Map[String, SecurityScheme] =
    val securityScheme = new SecurityScheme()
    securityScheme.setType(SecurityScheme.Type.HTTP)
    securityScheme.setScheme("bearer")
    securityScheme.setBearerFormat("JWT")
    securityScheme.setIn(SecurityScheme.In.HEADER)
    securityScheme.setName("Authorization")
    Map(BEARER_AUTH_SCHEME -> securityScheme)
