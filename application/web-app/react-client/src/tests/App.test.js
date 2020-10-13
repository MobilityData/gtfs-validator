/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import React from 'react';
import {shallow} from "enzyme";
import logo from "../logo.png";
import JsonExampleAccordion from "../components/JsonExampleAccordion";
import App from "../App";

test("renders main app without crashing", () => {
  shallow(<App/>);
});

test("App should contain mobility data logo", () => {
  let wrapper = shallow(<App/>);
  let mobilityDataLogo = <img src={logo} className="App-logo" alt="logo"/>;
  expect(wrapper.contains(mobilityDataLogo)).toBe(true);
})

test("App should contain JsonExampleAccordion", () => {
  let wrapper = shallow(<App/>);
  let jsonExampleAccordion = <JsonExampleAccordion className="json-example-container"/>
  expect(wrapper.contains(jsonExampleAccordion)).toBe(true);
})

test("App should contain container from json-content as table", () => {
  let wrapper = shallow(<App/>);
  let jsonContent = <div id="json-content"/>;
  expect(wrapper.contains(jsonContent)).toBe(true);
})

test("App should contain container for progress circle", () => {
  let wrapper = shallow(<App/>);
  let progressCircleContainer = <p id="progress-circles"/>;
  expect(wrapper.contains(progressCircleContainer)).toBe(true);
})

test("App should contain container for validation status", () => {
  let wrapper = shallow(<App/>);
  let validationStatusContainer = <p id="validation-status"/>;
  expect(wrapper.contains(validationStatusContainer)).toBe(true);
})

test("App should contain container for progress circle", () => {
  let wrapper = shallow(<App/>);
  let validationStatusContainer = <p id="validation-status"/>;
  expect(wrapper.contains(validationStatusContainer)).toBe(true);
})


test("App should contain link to mobilitydata.org", () => {
  let wrapper = shallow(<App/>);
  let linkToOrg =
      <p>
        <a className="App-link" href="https://mobilitydata.org">MobilityData</a>
      </p>
  expect(wrapper.contains(linkToOrg)).toBe(true);
})

test("App should contain link to documentation", () => {
  let wrapper = shallow(<App/>);
  let linkToDocumentation =
      <p>
        <a className="App-link" href="https://github.com/MobilityData/gtfs-validator">Project documentation</a>
      </p>
  expect(wrapper.contains(linkToDocumentation)).toBe(true);
})
