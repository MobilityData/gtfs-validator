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
import FittedButton from "./components/NiceButton";
import JsonDropzone from "./components/JsonDropzone";
import JsonRenderer from "./components/JsonRenderer";
import ReactDOM from 'react-dom';
import {initConfig, validateFeed} from "./helper/ApiRequest"

import {Port} from "./helper/Constants.js";

function App() {

  const onDrop = useCallback(acceptedFiles => {
    const fileReader = new FileReader();
    fileReader.readAsText(acceptedFiles[0])
    fileReader.onload = function () {
      alert("File successfully imported")
      displayJsonData(JSON.parse(fileReader.result), "json-content");
      initConfig(Port(), fileReader.result.toString())
    }
  }, []);

  function displayJsonData(jsonData, htmlDocumentId) {
    ReactDOM.render(
        <JsonRenderer data={jsonData} htmlId={htmlDocumentId}/>,
        document.getElementById(htmlDocumentId)
    );
  }

  return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo"/>
          <JsonDropzone id="json-config-file" onDrop={onDrop} accept={"application/json"}/>
          <div id="json-content"/>
          <p className="launch-button-container">
            <FittedButton description="Validate" method={validateFeed}/>
          </p>
          <p id="validation-status"/>
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
