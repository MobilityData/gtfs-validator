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
import ReactDOM from 'react-dom';

import FittedButton from "./components/NiceButton";
import JsonDropzone from "./components/JsonDropzone";
import JsonRenderer from "./components/JsonRenderer";
import JsonExampleAccordion from "./components/JsonExampleAccordion";

import './App.css';
import logo from './logo.png';

import {displayValidationReport, initConfig, validateFeed} from "./helper/ApiRequest"
import {Port} from "./helper/Constants.js";

function App() {

  const onDrop = useCallback(acceptedFiles => {
    clearHTML();
    deleteDisplayValidationReportButton();
    deleteTable();
    document.getElementById("json-content").style.visibility = "visible";
    if (!document.getElementById("json-config-file").hasAttribute("disabled")) {
      document.getElementById("validate-button").removeAttribute("disabled");
    }
    const fileReader = new FileReader();
    fileReader.readAsText(acceptedFiles[0])
    fileReader.onload = function () {
      if (!document.getElementById("json-config-file").hasAttribute("disabled")) {
        displayJsonData(JSON.parse(fileReader.result), "json-content");
        initConfig(Port(), JSON.parse(fileReader.result))
      } else {
        alert("Process already running, please wait for completion.")
      }
    }
  }, []);

  function displayJsonData(jsonData, htmlDocumentId) {
    ReactDOM.render(
        <JsonRenderer data={jsonData} htmlId={htmlDocumentId}/>,
        document.getElementById(htmlDocumentId)
    );
  }

  function clearHTML() {
    document.getElementById("display-result-button").style.visibility = "hidden";
    document.getElementById("validation-status").style.visibility = "hidden";
  }

  function deleteTable() {
    document.getElementById("json-content").style.visibility = "hidden";
  }

  function deleteDisplayValidationReportButton() {
    document.getElementById("display-result-button").style.visibility = "hidden";
  }

  return (
      <div className="App" onLoad={deleteDisplayValidationReportButton}>
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo"/>
          <JsonExampleAccordion className="json-example-container"/>
          <JsonDropzone id="json-config-file" onDrop={onDrop} accept={"application/json"}/>
          <div id="json-content"/>
          <p className="launch-button-container">
            <FittedButton id="validate-button" description="Validate" method={validateFeed}/>
          </p>
          <p id="progress-circles"/>
          <p id="validation-status"/>
          <div id="display-result-button" className="launch-button-container">
            <FittedButton description="Display validation report" method={displayValidationReport}/>
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
