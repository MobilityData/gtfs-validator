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

import React from "react";
import {shallow} from "enzyme";
import JsonExampleAccordion from "../JsonExampleAccordion";
import renderer from 'react-test-renderer';
import localJson from "../../data/json-examples/local-example.json";
import JsonBeautyfier from "../JsonBeautyfier";
import networkJson from "../../data/json-examples/network-example.json";

describe('NiceButton', () => {
    it('should be defined', () => {
        expect(JsonExampleAccordion).toBeDefined();
    });

    it('should render correctly', () => {
        const tree = renderer.create(
            <JsonExampleAccordion />
            ).toJSON();
        expect(tree).toMatchSnapshot();
    });

    it("should have two examples'", () => {
        const tree = shallow(
            <JsonExampleAccordion />
        )
        const firstExample = <JsonBeautyfier id="json-example" data={localJson} className="json-example"/>
        const secondExample = <JsonBeautyfier id="json-example" data={networkJson} className="json-example"/>;

        expect(tree.contains(firstExample)).toBe(true);
        expect(tree.contains(secondExample)).toBe(true);
    })

    // it("should call function when title item is clicked", () => {
    //     let mockOnClick = jest.fn();
    //     const tree = renderer.create(
    //         <JsonExampleAccordion />
    //     );
    //     tree.root.props.onClick();
    //     expect(mockOnClick).toHaveBeenCalled();
    // })
})
