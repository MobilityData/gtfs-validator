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
    console.log(acceptedFiles);
    var reader = new FileReader();
    reader.readAsText(acceptedFiles[0])
    reader.onload = function () {
      alert("File successfully loaded")
      // var test = document.getElementById("json-content")
      var obj = JSON.parse(reader.result);
      console.log(obj.help)
      // test.innerHTML = ""
      ReactDOM.render(<JsonRenderer data={obj} htmlId='json-content'/>,
          document.getElementById('json-content'));

      return reader.result;
    }
  }, []);

  return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo"/>
          <JsonDropZone id="gtfsarchive" onDrop={onDrop} accept={"application/json"}/>
          <div id="json-content"></div>
          <p className="launch-button-container">
            <FittedButton description="Validate" method={validate}/>
          </p>
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
