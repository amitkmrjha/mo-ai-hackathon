package com.bynder.clarify.ai

import com.clarifai.channel.ClarifaiChannel
import com.clarifai.credentials.ClarifaiCallCredentials
import com.clarifai.grpc.api.*
import com.clarifai.grpc.api.status.StatusCode
import com.google.protobuf.ByteString
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.io.File
import java.nio.file.Files
import scala.jdk.CollectionConverters.*

class ClarifyImageAISpec extends AnyFlatSpec with Matchers:
  // Your PAT (Personal Access Token) can be found in the portal under Authentication
  val PAT                      = "c73a8112593746a98484d1b8e5c9f8b5"
  // Specify the correct user_id/app_id pairings
  // Since you're making inferences outside your app's scope
  val USER_ID: String          = "amitjha"
  val APP_ID: String           = "eureka"
  // Change these to whatever model and image URL you want to use
  val MODEL_ID: String         = "general-image-recognition"
  val MODEL_VERSION_ID: String = "aa7f35c01e0642fda5cf400f543e7c40"
  val IMAGE_URL: String        = "https://samples.clarifai.com/metro-north.jpg"

  "Clarify AI Dummy Spec" should "equates true to true" in {
    true shouldEqual true
  }

  "Clarify AI " should "predict from url" in {

    val stub = V2Grpc
      .newBlockingStub(ClarifaiChannel.INSTANCE.getGrpcChannel)
      .withCallCredentials(new ClarifaiCallCredentials(PAT))

    val postModelOutputsResponse = stub.postModelOutputs(
      PostModelOutputsRequest.newBuilder
        .setUserAppId(UserAppIDSet.newBuilder.setUserId(USER_ID).setAppId(APP_ID))
        .setModelId(MODEL_ID)
        .setVersionId(MODEL_VERSION_ID)
        .addInputs // This is optional. Defaults to the latest model version.
        (Input.newBuilder.setData(Data.newBuilder.setImage(Image.newBuilder.setUrl(IMAGE_URL))))
        .build
    )

    if (postModelOutputsResponse.getStatus.getCode ne StatusCode.SUCCESS)
      throw new RuntimeException("Post model outputs failed, status: " + postModelOutputsResponse.getStatus)

    // Since we have one input, one output will exist here.
    val output = postModelOutputsResponse.getOutputs(0)

    output.getData.getConceptsList.size() > 0 shouldEqual true
  }

  "Clarify AI " should "predict from BytesString" in {
    val IMAGE_FILE_LOCATION =
      "/Users/amitkumar/Downloads/snap/Screenshots@2x.png"

    val stub = V2Grpc
      .newBlockingStub(ClarifaiChannel.INSTANCE.getGrpcChannel)
      .withCallCredentials(new ClarifaiCallCredentials(PAT))

    val postModelOutputsResponse = stub.postModelOutputs(
      PostModelOutputsRequest.newBuilder
        .setUserAppId(UserAppIDSet.newBuilder.setUserId(USER_ID).setAppId(APP_ID))
        .setModelId(MODEL_ID)
        .setVersionId(MODEL_VERSION_ID)
        .addInputs // This is optional. Defaults to the latest model version
        (
          Input.newBuilder.setData(
            Data.newBuilder.setImage(
              Image.newBuilder
                .setBase64(ByteString.copyFrom(Files.readAllBytes(new File(IMAGE_FILE_LOCATION).toPath)))
            )
          )
        )
        .build
    )

    if (postModelOutputsResponse.getStatus.getCode ne StatusCode.SUCCESS)
      throw new RuntimeException("Post model outputs failed, status: " + postModelOutputsResponse.getStatus)

    // Since we have one input, one output will exist here.
    val output = postModelOutputsResponse.getOutputs(0)
    output.getData.getConceptsList.forEach{concept =>
      println(s"${concept.getName}   ${concept.getValue}")
    }
    output.getData.getConceptsList.size() > 0 shouldEqual true
  }
