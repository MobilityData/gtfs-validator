import React from 'react';
import App from './App';
import {shallow} from "enzyme";
import JsonDropzone from "./components/JsonDropzone";
import FittedButton from "./components/NiceButton";

test("renders main app without crashing", () => {
  shallow(<App/>);
});

test("renders JsonDropzone component without crashing", () => {
  shallow(<JsonDropzone/>);
});

test("renders FittedButton component without crashing", () => {
  shallow(<FittedButton/>);
});
