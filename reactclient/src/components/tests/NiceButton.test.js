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
import NiceButton from "../NiceButton";
import renderer from 'react-test-renderer';

describe('NiceButton', () => {
    it('should be defined', () => {
        expect(NiceButton).toBeDefined();
    });

    it('should render correctly', () => {
        let mockOnClick = jest.fn();
        const tree = renderer.create(
            <NiceButton id='id value' onClick={mockOnClick} description="description value"/>
        ).toJSON();
        expect(tree).toMatchSnapshot();
    });

    it('should call function when button is clicked', () => {
        let mockOnClick = jest.fn();
        const tree = renderer.create(
            <NiceButton id='id value' onClick={mockOnClick} description="description value"/>
        );
        tree.root.props.onClick();
        expect(mockOnClick).toHaveBeenCalledTimes(1);
    });
});
