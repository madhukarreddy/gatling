/**
 * Copyright 2011-2015 eBusiness Information, Groupe Excilys (www.ebusinessinformation.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gatling.core.funspec

import io.gatling.core.Predef._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.Protocol
import io.gatling.core.structure.ChainBuilder

import scala.collection.mutable.ListBuffer

abstract class GatlingFunSpec extends Simulation {

  /** Set the protocol configuration used by gatling to execute all specs in this class */
  def protocolConf: Protocol

  /** Add a spec to be executed */
  def spec(actionBuilder: ActionBuilder) = specs += actionBuilder

  private[this] val specs = new ListBuffer[ActionBuilder]

  private[this] lazy val testScenario = scenario(this.getClass.getSimpleName)
    .exec(ChainBuilder(specs.reverse.toList))

  private[core] def setupRegisteredSpecs = {
    require(specs.length > 0, "At least one spec needs to be defined")
    setUp(testScenario.inject(atOnceUsers(1)))
      .protocols(protocolConf)
      .assertions(forAll.failedRequests.percent.is(0))
  }

}
