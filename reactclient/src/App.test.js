import React from 'react';
import App from './App';
import {shallow} from "enzyme";
import logo from "./logo.png";
import JsonExampleAccordion from "./components/JsonExampleAccordion";

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
