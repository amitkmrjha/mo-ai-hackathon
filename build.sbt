import com.typesafe.sbt.packager.docker._
import BuildSettings._
import Dependencies._

val commonDeps          = Seq(scalaTest, scalaCheck, logback, logbackJson, logbackJackson)
val akkaDeps            = Seq(akkaTyped, akkaSLF4J, akkaTypedTestKit)
val gatlingDeps         = Seq(gatlingHighCharts, gatlingTestFramework)
val jacksonDeps         = Seq(jacksonCore, jacksonModuleScala)
val akkDiscoveryDeps    = Seq(akkaDiscovery, akkaDiscoveryKubernetes)
val akkaHttpDeps        =
  Seq(
    akkaHttp,
    akkaHttpXml,
    sprayJson,
    akkaHttpSprayJson,
    akkaHttpTestKit,
    akkaPKI,
    swaggerAkkaHttp,
    swaggerAkkaHttpScala,
    swaggerAkkaHttpEnum,
    jacksonModuleScala,
    jakartaWsRs,
    swaggerCore
  )
val akkaStreamsDeps     = Seq(akkaStreams, akkaStreamTyped, akkaStreamsTestKit)
val akkaClusterDeps     = Seq(
  akkaClusterShardingTyped,
  akkaManagement,
  akkaManagementClusterBootstrap,
  akkaManagementClusterHTTP,
  akkaHttpSprayJson
) ++ akkDiscoveryDeps
val akkaPersistenceDeps =
  Seq(
    akkaPersistence,
    akkaPersistenceTestKit,
    akkaPersistenceJdbc,
    akkaPersistenceQuery,
    akkaJackson,
    postgres,
    iamPostgresJDBCDriver
  ) ++ jacksonDeps
val scalaLikeJdbcDeps   = Seq(
  scalikeJDBC,
  scalikeJDBCStream,
  scalikeJDBCConfig,
  scalikeJDBCTest,
  sslConfig
)
val alpakkaKafkaDeps    = Seq(
  alpakkaKafka,
  alpakkaKafkaTestKit
)
val akkaProjectionDeps  = Seq(
  akkaPersistenceEventSourcedProjection,
  akkaDurableStateProjection,
  akkaJDBCProjection,
  akkaKafkaProjection
) ++ alpakkaKafkaDeps

val s3Deps =
  Seq(
    alpakkaS3,
    scalapbRuntime,
    awsS3,
    awsSTS,
    akkaHttpXml,
    scalaXml
  )

val alpakkaSlickDeps =
  Seq(
    alpakkaSlick,
    scalapbRuntime
  )
val monitoringDeps   = Seq(kamonBundle, kamonPrometheus, kamonOpentelemetry)

val excludeProtobufConflictDeps = Seq(
  ExclusionRule(
    "com.thesamet.scalapb",
    "scalapb-runtime_3"
  ),
  ExclusionRule(
    "org.scala-lang.modules",
    "scala-collection-compat_3"
  )
)

val excludeXmlConflictDeps = Seq(
  ExclusionRule(
    "org.scala-lang.modules",
    "scala-xml_2.13"
  )
)

val excludeAkkaXml = Seq(
  ExclusionRule(
    "com.typesafe.akka",
    "akka-http-xml_2.13"
  )
)

lazy val dockerSettings = Seq(
  Docker / maintainer      := "clarify-init",
  dockerBaseImage          := sys.env.getOrElse("JDK_DOCKER_BASE_IMAGE", "openjdk:17-bullseye"),
  dockerPermissionStrategy := DockerPermissionStrategy.MultiStage
)

lazy val `mo-ai-hackathon` = (project in file("."))
  .settings(buildSettings: _*)
  .aggregate(
    `clarify-image`,
    `clarify-ai-protobuf`,
    `clarify-ai-common`

  )
lazy val `clarify-ai-common`            = (project in file("clarify-ai-common"))
  .configs(IntegrationTest)
  .settings(buildSettings: _*)
  .settings(
    libraryDependencies ++= commonDeps ++ jacksonDeps
  )


lazy val `clarify-ai-protobuf` = (project in file("clarify-ai-protobuf"))
  .enablePlugins(AkkaGrpcPlugin)
  .settings(
    libraryDependencies += scalapbRuntime,
    excludeDependencies ++= excludeProtobufConflictDeps
  )

lazy val `clarify-image`            = (project in file("clarify-image"))
  .configs(IntegrationTest)
  .settings(buildSettings: _*)
  .settings(
    libraryDependencies ++= commonDeps ++ Seq(clarifyAI)
).dependsOn(`clarify-ai-common`)