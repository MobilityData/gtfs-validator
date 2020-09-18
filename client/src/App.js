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
import logo from './logo.png';
import './App.css';
import InputField from "./components/input";
import FittedButton from "./components/button";

function App() {

  async function onClick() {
    alert(document.getElementById("command-line-input").value);
    let response = await fetch('http://localhost:8080/report');
    let body = await response.text();
    console.log(body);
  }

  return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo"/>
          <p className="command-line-container">
            <InputField placeHolderText="Enter command line here" id="command-line-input" type="text"/>
          </p>
          <p className="launch-button-container">
            <FittedButton description="Launch validation process" method={onClick}/>
          </p>
          <p>
            <a
                className="App-link"
                href="https://mobilitydata.org"
                target="_blank"
                rel="noopener noreferrer"
            >
              MobilityData
            </a>
          </p>
          <p>
            <a
                className="App-link"
                href="https://github.com/MobilityData/gtfs-validator"
                target="_blank"
                rel="noopener noreferrer"
            >
              Project documentation
            </a>
          </p>
        </header>
      </div>
  );
}

export default App;
