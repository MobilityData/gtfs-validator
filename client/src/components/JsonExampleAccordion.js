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

import React, {Component} from 'react'
import {Accordion, Icon} from 'semantic-ui-react'
import JsonBeautyfier from "./JsonBeautyfier";
import localJson from "../data/json-examples/local-example.json";
import networkJson from "../data/json-examples/network-example.json";

export default class JsonExampleAccordion extends Component {
    state = {activeIndex: 0}

    handleClick = (e, titleProps) => {
        const {index} = titleProps
        const {activeIndex} = this.state
        const newIndex = activeIndex === index ? -1 : index

        this.setState({activeIndex: newIndex})
    }

    render() {
        const {activeIndex} = this.state;
        return (
            <Accordion styled id="json-example" className="json-example-container">
                <Accordion.Title
                    active={activeIndex === 0}
                    index={0}
                    onClick={this.handleClick}
                    className="json-example-tile"
                >
                    <Icon name='dropdown'/>
                    Example of configuration file using URL to remote GTFS archive </Accordion.Title>
                <Accordion.Content active={activeIndex === 0}>
                    <JsonBeautyfier id="json-example" data={localJson} className="json-example"/>

                </Accordion.Content>

                <Accordion.Title
                    active={activeIndex === 1}
                    index={1}
                    onClick={this.handleClick}
                >
                    <Icon name='dropdown'/>
                    Example of configuration file using locally stored GTFS archive
                </Accordion.Title>
                <Accordion.Content active={activeIndex === 1}>
                    <JsonBeautyfier id="json-example" data={networkJson} className="json-example"/>
                </Accordion.Content>
            </Accordion>
        )
    }
}
