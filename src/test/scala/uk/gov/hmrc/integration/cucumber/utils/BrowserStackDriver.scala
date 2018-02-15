/*
 * Copyright 2016 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.integration.cucumber.utils

import java.io.{FileNotFoundException, IOException}
import java.net.URL
import java.util.Properties

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.{DesiredCapabilities, RemoteWebDriver}

import scala.io.Source
import scala.collection.JavaConversions._

trait BrowserStackDriver {
  self: Driver =>

  private val DRIVER_INFO_FLAG = false

  def browserStackSetup(capabilities: DesiredCapabilities): WebDriver = {

    var userName: String = null
    var automateKey: String = null

    try {
      val prop: Properties = new Properties()
      prop.load(this.getClass.getResourceAsStream("/browserConfig.properties"))

      userName = prop.getProperty("username")
      automateKey = prop.getProperty("automatekey")
    }
    catch {
      case e: FileNotFoundException => e.printStackTrace();
      case e: IOException => e.printStackTrace();
    }

    capabilities.setCapability("browserstack.debug", "true")
    capabilities.setCapability("browserstack.local", "true")
    capabilities.setCapability("project", "Template")
    capabilities.setCapability("build", "Template Build_1.0") //?????

    val bsUrl = s"http://$userName:$automateKey@hub-cloud.browserstack.com/wd/hub"
    val rwd = new RemoteWebDriver(new URL(bsUrl), capabilities)
    printCapabilities(rwd, DRIVER_INFO_FLAG)
    rwd
  }

  def getBrowserStackCapabilities: Map[String, Object] = {
    val testDevice = System.getProperty("testDevice", "BS_Win8_Chrome_64")
    val resourceUrl = s"/browserstackdata/$testDevice.json"
    val cfgJsonString = Source.fromURL(getClass.getResource(resourceUrl)).mkString

    val mapper = new ObjectMapper() with ScalaObjectMapper
    mapper.registerModule(DefaultScalaModule)
    mapper.readValue[Map[String, Object]](cfgJsonString)
  }

  def printCapabilities(rwd: RemoteWebDriver, fullDump: Boolean): Unit = {
    var key = ""
    var value: Any = null

    println("RemoteWebDriver Basic Capabilities >>>>>>")
    val caps = rwd.getCapabilities
    val platform = caps.getPlatform
    println(s"platform : $platform")
    val browserName = caps.getBrowserName
    println(s"browserName : $browserName")
    val version = caps.getVersion
    println(s"version : $version")

    val capsMap = caps.asMap()
    val basicKeyList = List("os", "os_version", "mobile", "device", "deviceName")
    for (key <- basicKeyList) {
      if (capsMap.containsKey(key)) {
        value = capsMap.get(key)
        println(s"$key : $value")
      } else {
        println(s"$key : not set")

      }
    }

    if (fullDump) {
      println("Full Details >>>>>>")
      for (key <- capsMap.keySet()) {
        value = capsMap.get(key)
        println(s"$key : $value")
      }
    }
  }

}
