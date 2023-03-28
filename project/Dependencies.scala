import sbt._

object Dependencies {


  val scalaTest  = "org.scalatest"  %% "scalatest"  % Version.scalaTest  % "it, test"
  val scalaCheck = "org.scalacheck" %% "scalacheck" % Version.scalaCheck % "it, test"
  val scalaMeta =  "org.scalameta" %% "munit" % Version.scalameta % "it, test"

  val logback        = "ch.qos.logback"         % "logback-classic"      % Version.logback
  val logbackJson    = "ch.qos.logback.contrib" % "logback-json-classic" % Version.logbackContrib
  val logbackJackson = "ch.qos.logback.contrib" % "logback-jackson"      % Version.logbackContrib

  val akkaTyped        = ("com.typesafe.akka" %% "akka-actor-typed" % Version.akka).cross(CrossVersion.for3Use2_13)
  val akkaTypedTestKit =
    ("com.typesafe.akka" %% "akka-actor-testkit-typed" % Version.akka % "it, test").cross(CrossVersion.for3Use2_13)
  val akkaClusterShardingTyped =
    ("com.typesafe.akka" %% "akka-cluster-sharding-typed" % Version.akka).cross(CrossVersion.for3Use2_13)
  val alpakkaClusterSharding =
    ("com.typesafe.akka" %% "akka-stream-kafka-cluster-sharding" % Version.alpakkaKafka).cross(CrossVersion.for3Use2_13)
  val akkaPersistence        = ("com.typesafe.akka" %% "akka-persistence-typed" % Version.akka).cross(CrossVersion.for3Use2_13)
  val akkaPersistenceTestKit =
    ("com.typesafe.akka" %% "akka-persistence-testkit" % Version.akka % "it, test").cross(CrossVersion.for3Use2_13)
  val akkaPersistenceJdbc =
    ("com.lightbend.akka" %% "akka-persistence-jdbc" % Version.akkaPersistenceJdbc).cross(CrossVersion.for3Use2_13)
  val akkaPersistenceQuery =
    ("com.typesafe.akka" %% "akka-persistence-query" % Version.akka).cross(CrossVersion.for3Use2_13)
  val postgres           = "org.postgresql"      % "postgresql"        % Version.postgres
  val akkaStreams        = ("com.typesafe.akka" %% "akka-stream"       % Version.akka).cross(CrossVersion.for3Use2_13)
  val akkaStreamTyped    = ("com.typesafe.akka" %% "akka-stream-typed" % Version.akka).cross(CrossVersion.for3Use2_13)
  val akkaStreamsTestKit =
    ("com.typesafe.akka" %% "akka-stream-testkit" % Version.akka % "it, test").cross(CrossVersion.for3Use2_13)
  val akkaSLF4J    = ("com.typesafe.akka"  %% "akka-actor"             % Version.akka).cross(CrossVersion.for3Use2_13)
  val alpakkaS3    = ("com.lightbend.akka" %% "akka-stream-alpakka-s3" % Version.alpakkaS3).cross(CrossVersion.for3Use2_13)
  val alpakkaSlick =
    ("com.lightbend.akka" %% "akka-stream-alpakka-slick" % Version.alpakkaSlick).cross(CrossVersion.for3Use2_13)
  val akkaHttp        = ("com.typesafe.akka" %% "akka-http" % Version.akkaHttp).cross(CrossVersion.for3Use2_13)
  val akkaHttpTestKit =
    ("com.typesafe.akka" %% "akka-http-testkit" % Version.akkaHttp % "it, test").cross(CrossVersion.for3Use2_13)
  val akkaPKI                 = ("com.typesafe.akka" %% "akka-pki"       % Version.akka).cross(CrossVersion.for3Use2_13)
  val akkaHttpXml             = ("com.typesafe.akka" %% "akka-http-xml"  % Version.akkaHttp).cross(CrossVersion.for3Use2_13)
  val akkaDiscovery           = ("com.typesafe.akka" %% "akka-discovery" % Version.akka).cross(CrossVersion.for3Use2_13)
  val akkaDiscoveryKubernetes =
    ("com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % Version.akkaManagement).cross(
      CrossVersion.for3Use2_13
    )
  val akkaManagement =
    ("com.lightbend.akka.management" %% "akka-management" % Version.akkaManagement).cross(CrossVersion.for3Use2_13)
  val akkaManagementClusterHTTP =
    ("com.lightbend.akka.management" %% "akka-management-cluster-http" % Version.akkaManagement).cross(
      CrossVersion.for3Use2_13
    )
  val akkaManagementClusterBootstrap =
    ("com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % Version.akkaManagement).cross(
      CrossVersion.for3Use2_13
    )
  val akkaJackson           = ("com.typesafe.akka"             %% "akka-serialization-jackson" % Version.akka).cross(CrossVersion.for3Use2_13)
  val jacksonCore           = ("com.fasterxml.jackson.core"     % "jackson-databind"           % Version.jackson)
  val JacksonDataTypeJSR310 = ("com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310"    % Version.jackson)
  val JacksonModuleEnum     = "com.github.pjfanning"           %% "jackson-module-scala3-enum" % Version.jacksonScala3Enum
  val jacksonModuleScala    = ("com.fasterxml.jackson.module"  %% "jackson-module-scala"       % Version.jackson)
    .cross(CrossVersion.for3Use2_13)
  val sprayJson             = ("io.spray"                      %% "spray-json"                 % Version.spray).cross(CrossVersion.for3Use2_13)
  val akkaHttpSprayJson     =
    ("com.typesafe.akka" %% "akka-http-spray-json" % Version.akkaHttp).cross(CrossVersion.for3Use2_13)
  val scalapbRuntime =
    ("com.thesamet.scalapb" %% "scalapb-runtime" % Version.scalaPB % "protobuf").cross(CrossVersion.for3Use2_13)
  val scalapbPlugin =
    ("com.thesamet.scalapb" %% "compilerplugin" % Version.scalaPB).cross(CrossVersion.for3Use2_13)

  val scalapbJson   = ("com.thesamet.scalapb" %% "scalapb-json4s" % Version.scalaPBJson).cross(CrossVersion.for3Use2_13)
  val json4sJackson = ("org.json4s"           %% "json4s-jackson" % Version.json4s).cross(CrossVersion.for3Use2_13)

  val swaggerAkkaHttp =
    ("com.github.swagger-akka-http" %% "swagger-akka-http" % Version.swaggerAkkaHttp).cross(CrossVersion.for3Use2_13)
  val swaggerAkkaHttpScala =
    ("com.github.swagger-akka-http" %% "swagger-scala-module" % Version.swaggerAkkaHttpScala).cross(
      CrossVersion.for3Use2_13
    )
  val swaggerAkkaHttpEnum =
    ("com.github.swagger-akka-http" %% "swagger-enumeratum-module" % Version.swaggerAkkaHttpEnum).cross(
      CrossVersion.for3Use2_13
    )
  val jakartaWsRs = "jakarta.ws.rs"      % "jakarta.ws.rs-api"      % Version.jakartaWsRs
  val swaggerCore = "io.swagger.core.v3" % "swagger-jaxrs2-jakarta" % Version.swaggerCore

  val awsS3            = "software.amazon.awssdk" % "s3"             % Version.aws
  val awsSTS           = "software.amazon.awssdk" % "sts"            % Version.awsSTS
  val awsSecretManager = "software.amazon.awssdk" % "secretsmanager" % Version.aws

  val scalaXml           = "org.scala-lang.modules" %% "scala-xml"           % Version.scalaXml
  val kamonBundle        = ("io.kamon"              %% "kamon-bundle"        % Version.kamon).cross(CrossVersion.for3Use2_13)
  val kamonPrometheus    = ("io.kamon"              %% "kamon-prometheus"    % Version.kamon).cross(CrossVersion.for3Use2_13)
  val kamonOpentelemetry = ("io.kamon"              %% "kamon-opentelemetry" % Version.kamon).cross(CrossVersion.for3Use2_13)
  val kanelaAgent        = "io.kamon"                % "kanela-agent"        % Version.kanelaAgent

  val bouncyCastle = "org.bouncycastle" % "bcprov-jdk15on" % Version.bouncyCastle

  val alpakkaKafka        = ("com.typesafe.akka" %% "akka-stream-kafka" % Version.alpakkaKafka).cross(CrossVersion.for3Use2_13)
  val alpakkaKafkaTestKit =
    ("com.typesafe.akka" %% "akka-stream-kafka-testkit" % Version.alpakkaKafka % "it, test")
      .cross(CrossVersion.for3Use2_13)

  val akkaPersistenceEventSourcedProjection =
    ("com.lightbend.akka" %% "akka-projection-eventsourced" % Version.akkaProjection).cross(CrossVersion.for3Use2_13)
  val akkaDurableStateProjection =
    ("com.lightbend.akka" %% "akka-projection-durable-state" % Version.akkaProjection).cross(CrossVersion.for3Use2_13)
  val akkaJDBCProjection =
    ("com.lightbend.akka" %% "akka-projection-jdbc" % Version.akkaProjection).cross(CrossVersion.for3Use2_13)
  val akkaKafkaProjection =
    ("com.lightbend.akka" %% "akka-projection-kafka" % Version.akkaProjection).cross(CrossVersion.for3Use2_13)

  val scalikeJDBC       = ("org.scalikejdbc" %% "scalikejdbc" % Version.scalikeJDBC).cross(CrossVersion.for3Use2_13)
  val scalikeJDBCStream =
    ("org.scalikejdbc" %% "scalikejdbc-streams" % Version.scalikeJDBCStream).cross(CrossVersion.for3Use2_13)
  val scalikeJDBCConfig =
    ("org.scalikejdbc" %% "scalikejdbc-config" % Version.scalikeJDBC).cross(CrossVersion.for3Use2_13)
  val scalikeJDBCTest =
    ("org.scalikejdbc" %% "scalikejdbc-test" % Version.scalikeJDBC % "test,it").cross(CrossVersion.for3Use2_13)

  val sslConfig = ("com.typesafe" %% "ssl-config-core" % Version.sslConfig).cross(CrossVersion.for3Use2_13)

  val cats                  = "org.typelevel" %% "cats-core"       % Version.cats
  val iamPostgresJDBCDriver = "io.magj"        % "iam-jdbc-driver" % Version.iamPostgresJDBC

  val gatlingHighCharts    = "io.gatling.highcharts" % "gatling-charts-highcharts" % Version.gatling     % "test,it"
  val gatlingTestFramework = "io.gatling"            % "gatling-test-framework"    % Version.gatling     % "test,it"
  val gatlingGrpc          = "com.github.phisgr"     % "gatling-grpc"              % Version.gatlingGrpc % "test, it"
  val nettyGrpc            = "io.grpc"               % "grpc-netty"                % scalapb.compiler.Version.grpcJavaVersion

  val googleCommonProtos = Seq(
    "com.thesamet.scalapb.common-protos" %% "proto-google-common-protos-scalapb_0.11" % Version.googleCommonProtos % "protobuf",
    "com.thesamet.scalapb.common-protos" %% "proto-google-common-protos-scalapb_0.11" % Version.googleCommonProtos
  )
  val clarifyAI =  "com.clarifai" % "clarifai-grpc" % Version.clarify

}
