import React from 'react';
import logo from './logo.png';
import './App.css';
import InputField from "./components/input";
import FittedButton from "./components/button";

function App() {
  return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo"/>
          <p>
            Add text bar here for command line
          </p>
          <p>
            <InputField placeHolderText="Enter command line here" id="command-line-input"/>
          </p>
          <p>
            <FittedButton description="Lauch validation process"/>
          </p>
          <p>
            <a
                className="App-link"
                href="https://mobilitydata.org"
                target="_blank"
                rel="noopener noreferrer"
            >
              MobilityData
            </a></p>
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
