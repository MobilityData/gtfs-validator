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

import {openReport, initializeConfig, runValidator} from "./helper/ApiRequest"
import {displayJsonData, enableElement, hasAttribute, hideItem} from "./helper/DomInteractions";
import {InitConfigCommand, Port} from "./helper/Constants";
import {useDropzone} from "react-dropzone";

function App() {
  const onDrop = useCallback(acceptedFiles => {
    if (!hasAttribute("json-config-file", "disabled")) {
      enableElement("validate-button");
    } else {
      hideItem("display-result-button");
      hideItem("open-validation-dom-element");
    }
    const fileReader = new FileReader();
    fileReader.readAsText(acceptedFiles[0])
    fileReader.onload = function () {
      if (!hasAttribute("json-config-file", "disabled")) {
        console.log(hasAttribute("json-config-file", "disabled"))
        displayJsonData(JSON.parse(fileReader.result), "json-content");
        initializeConfig(axios, Port(), InitConfigCommand(), JSON.parse(fileReader.result))
        hideItem("validation-status");
        hideItem("display-result-button");
      } else {
        console.log(hasAttribute("json-config-file", "disabled"))
        alert("Process already running, please wait for completion.")
      }
    }
  }, []);

  return (
      <div className="App" onLoad={() => hideItem("display-result-button")}>
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo"/>
          <JsonExampleAccordion className="json-example-container"/>
          <JsonDropzone id="json-config-file" onDrop={onDrop} accept={"application/json"} useDropzone={useDropzone}/>
          <div id="json-content"/>
          <p className="launch-button-container">
            <NiceButton id="validate-button" description="Validate" method={runValidator}/>
          </p>
          <p id="progress-circles"/>
          <p id="validation-status"/>
          <p id="open-validation-dom-element"/>
          <div id="display-result-button" className="launch-button-container">
            <NiceButton description="Display validation report" method={openReport}/>
          </div>
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
