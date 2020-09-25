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
import logo from './logo.png';
import './App.css';
import FittedButton from "./components/button";
import JsonDropZone from "./components/jsonDropzone";
import JsonRenderer from "./components/json-renderer";
import ReactDOM from 'react-dom';
import JsonBeautyfier from "./components/jsonPrettyfier";

function App() {

  async function loadConfigFile() {
  }

  async function validate() {
    alert(document.getElementById("command-line-input").value);
    let response = await fetch('http://localhost:8090/report');
    let body = await response.text();
    console.log(body);
  }

  const onDrop = useCallback(acceptedFiles => {
    const fileReader = new FileReader();
    fileReader.readAsText(acceptedFiles[0])
    fileReader.onload = function () {
      alert("File successfully imported")
      displayJsonData(JSON.parse(fileReader.result), "json-content");
      reviewExecParamConfigFileContent(fileReader.result.toString())
    }
  }, []);

  function displayJsonData(jsonData, htmlDocumentId) {
    ReactDOM.render(
        <JsonRenderer data={jsonData} htmlId={htmlDocumentId}/>,
        document.getElementById(htmlDocumentId)
    );
  }

  async function reviewExecParamConfigFileContent(execParamConfigFileAsString) {
    let response = await fetch('http://localhost:8090/actions/initconfig/' + execParamConfigFileAsString);
    let body = await response.text();
    console.log(body);
  }

  async function validateFeed() {
    let response = await fetch('http://localhost:8090/actions/runvalidator');
    let body = await response.text();
    ReactDOM.render(
        <p>{body}</p>,
        document.getElementById("validation"))
    let reportResponse = await fetch('http://localhost:8090/actions/getreportcontent');
    let reportBody = await reportResponse.text();
    ReactDOM.render(
        <JsonBeautyfier data={JSON.parse(reportBody)}/>,
        document.getElementById("report"))
  }

  return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo"/>
          <JsonDropZone id="gtfsarchive" onDrop={onDrop} accept={"application/json"}/>
          <div id="json-content"/>
          <p className="launch-button-container">
            <FittedButton description="Validate" method={validateFeed}/>
          </p>
          <p id="validation"/>
          <p id="report" className="report"/>
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
