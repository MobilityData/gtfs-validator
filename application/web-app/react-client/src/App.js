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

import React, {useCallback} from "react";
import axios from "axios";

import NiceButton from "./components/NiceButton";
import JsonDropzone from "./components/JsonDropzone";
import JsonExampleAccordion from "./components/JsonExampleAccordion";

import './App.css';
import logo from './logo.png';

import ApiRequestExecutor from "./helper/ApiRequestExecutor";
import DomInteractionExecutor from "./helper/DomInteractionsExecutor";
import {InitConfigCommand, Port} from "./helper/Constants";
import {useDropzone} from "react-dropzone";

/**
 * Main object rendered on the one pager
 * @returns {JSX.Element} the DOM element to be rendered
 * @constructor
 */
function App() {
  /**
   * Defines actions when a file is dropped in a dropzone component
   * @type {function(*): void}
   */
  const onDrop = useCallback(acceptedFiles => {
    if (!DomInteractionExecutor.hasAttribute("json-config-file", "disabled")) {
      DomInteractionExecutor.enableElement("validate-button");
      DomInteractionExecutor.hideItem("validation-report-view-container");
    } else {
      DomInteractionExecutor.hideItem("display-result-button");
      DomInteractionExecutor.hideItem("open-validation-dom-element");
    }
    const fileReader = new FileReader();
    fileReader.readAsText(acceptedFiles[0])
    fileReader.onload = function () {
      if (!DomInteractionExecutor.hasAttribute("json-config-file", "disabled")) {
        console.log(DomInteractionExecutor.hasAttribute("json-config-file", "disabled"))
        DomInteractionExecutor.displayJsonData(JSON.parse(fileReader.result), "json-content");
        ApiRequestExecutor.initializeConfig(axios, Port(), InitConfigCommand(), JSON.parse(fileReader.result))
        DomInteractionExecutor.hideItem("validation-status");
        DomInteractionExecutor.hideItem("display-result-button");
      } else {
        alert("Process already running, please wait for completion.")
      }
    }
  }, []);

  return (
      <div className="App" onLoad={() => DomInteractionExecutor.hideItem("display-result-button")}>
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo"/>
          <JsonExampleAccordion className="json-example-container"/>
          <JsonDropzone id="json-config-file" onDrop={onDrop} accept={"application/json"} useDropzone={useDropzone}/>
          <div id="json-content"/>
          <p className="launch-button-container">
            <NiceButton id="validate-button" description="Validate" method={ApiRequestExecutor.runValidator}/>
          </p>
          <p id="progress-circles"/>
          <p id="validation-status"/>
          <p id="open-validation-dom-element"/>
          <div id="display-result-button" className="launch-button-container">
            <NiceButton description="Display validation report" method={ApiRequestExecutor.displayReport}/>
          </div>
          <p id="validation-report-view-container"/>
          <p>
            <a className="App-link" href="https://mobilitydata.org">MobilityData</a>
          </p>
          <p>
            <a className="App-link" href="https://github.com/MobilityData/gtfs-validator">Project documentation</a>
          </p>
        </header>
      </div>
  );
}

export default App;
