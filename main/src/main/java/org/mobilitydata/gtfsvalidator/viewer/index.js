let h = snabbdom.h // For convenience.

ReportView = {
    // Report Viewer State.
    container: null, // The DOM node we render into. Used for the first render.
    vnode: null, // Previous vnode. Snabbdom uses this to calculate the diff.
    showNotice: report.notices.map(()=>false), // Should this notice # be shown?

    patch: snabbdom.init([ 
        // Initialize Snabbdom patch function with chosen modules.
        snabbdom.classModule, // Makes it easy to toggle classes.
        snabbdom.propsModule, // For setting properties on DOM elements.
        snabbdom.styleModule, // Handles styling on elements with support for animations.
        snabbdom.eventListenersModule, // Attaches event listeners.
    ]),

    showNoticeButton: function(i) {
        // Toggle button to show/hide a notice #.
        if (ReportView.showNotice[i]) {
            return h('button',
                { on: { click: () => ReportView.toggleShowNotice(i) } },
                'Hide Details <<'
            )
        } else {
            return h('button',
                { on: { click: () => ReportView.toggleShowNotice(i) } },
                'Show Details >> '
            )
        }
    },

    toggleShowNotice: i => {
        console.log("toggleShowNotice(", i, ")")
        ReportView.showNotice[i] = ! ReportView.showNotice[i]
        ReportView.render()
    },

    view: () => h("div",
        // The global variable "report" is defined in report.json.js, included
        // by the index.html page.
        report.notices.map((x,i) => {
            let severity_class = "severity-unknown"
            let header_text = x.code 
            if (x.severity != null) {
                // Apply a corresponding CSS class for this severity.
                severity_class = "severity-" + x.severity.toLowerCase()
                header_text = x.code + " (" + x.severity.toLowerCase() + ")"
            }
            return h("div", [
                h(`h2.${severity_class}`, header_text),
                ReportView.showNoticeButton(i),
                ReportView.showNotice[i] ? NoticeDetails.view(x) : h("!", "This notice is currently hidden."),
            ])
        }),
    ),

    render: function()  {
        if (ReportView.container == null) {
            // Initialize container to the DOM element on our first call.
            ReportView.container = document.getElementById("container");
        }
        if (ReportView.vnode == null) {
            // Initialize vnode to the empty DOM element on our first call.
            ReportView.vnode = ReportView.container
        }
        let newVnode = ReportView.view()
        ReportView.patch(ReportView.vnode, newVnode);
        ReportView.vnode = newVnode
    }
}

// Multiple error reports use this field combination.
var NoticeDetailsGenericFieldValueCodeView = n => h("ul",
    n.sampleNotices.map(y => h("li", 
        `column: ${y.fieldName} value:"${y.fieldValue}" (${y.filename}:${y.csvRowNumber}) `,
    ))) 

var NoticeDetails = {
    view: function (notice) {
        // View for a single notice within the report.
        if (NoticeDetails.codeView[notice.code] != null) {
            // Use a specialized view for this code if one exists.
            return NoticeDetails.codeView[notice.code](notice)
        }
        // Otherwise use a a generic view.
        return NoticeDetails.genericCodeView(notice)
    },

    genericCodeView: (notice) => h("div", {}, [
        h("ul",
            notice.sampleNotices.map((y) => {
                let filename = y.filename
                let csvRowNumber = y.csvRowNumber
                if (filename == null) {
                    filename = 'unspecified filename'
                }
                if (csvRowNumber == null) {
                    csvRowNumber = ''
                }
                return h("li", `(${y.filename}:${y.csvRowNumber})`)
            },
            )), 
        h("pre", JSON.stringify(notice, true, "  ")),
    ]),

    codeView: {
        // Functions indexed by notice code. See gtfs-validator/docs/NOTICES.md.
        //
        
        attribution_without_role: n => h("div", [
            h("p", "Validates that an attribution has at least one role: is_producer, is_operator, or is_authority."),
            h("ul",
            n.sampleNotices.map(y => h("li", 
                `attribution_id: ${y.attributionId} (attributions.txt:${y.csvRowNumber}) `,
            )))
        ]),

        block_trips_with_overlapping_stop_times: n => {
            let filename = "trips.txt"
            return h("div", [
                h("p", "Describes two trips with the same block id that have overlapping stop times."),
                h("ul",
                    n.sampleNotices.map(y => h("li", `
                        block ${y.blockId} used by trip_id ${y.tripIdA} (${y.filename}:${y.csvRowNumberA}) 
                        overlaps on ${y.intersection} with
                        use by trip_id ${y.tripIdB} (${y.filename}:${y.csvRowNumberB}) 
                        `)))
            ])
        },

        csv_parsing_failed: n => h("ul",
            n.sampleNotices.map(y => h("li", `At char ${y.charIndex} of ${y.filename}:${y.lineIndex}, ${y.message}`))
        ),

        decreasing_or_equal_shape_distance: n=> {
            let filename = "shapes.txt"
            return h("div", [
                h("p", "When sorted on `shapes.shape_pt_sequence` key, shape points should have strictly increasing values for `shapes.shape_dist_traveled"),
                h("p", "Values must increase along with shape_pt_sequence. For more information, please vist:"),
                h("a", { props: { href: "http://gtfs.org/reference/static/#shapestxt." } }, "http://gtfs.org/reference/static/#shapestxt."),
                h("p",
                    n.sampleNotices.map(y => h("", [
                        h("p", { style: { fontWeight: "bold"} }, `shape_id: ${y.shapeId}`),
                        h("ul", [
                            h("li", ` shape_pt_sequence=${y.shapePtSequence} (${y.filename}:${y.csvRowNumber})`),
                            h("li", `distance ${y.shapeDistTraveled} is <= ${y.prevShapeDistTraveled} at shape_pt_sequence=${y.prevShapePtSequence} (${y.filename}:${y.prevCsvRowNumber})`)
                        ])
                    ]
                )))
            ])
        },

        decreasing_or_equal_stop_time_distance: n => {
            let filename = "stop_times.txt"
            return h("div", [
                h("p", "When sorted on `stops.stop_sequence` key, stop times should have strictly increasing values for `stops.shape_dist_traveled`"),
                h("p", "Values used for shape_dist_traveled must increase along with stop_sequence. For more information, please vist:"),
                h("a", { props: { href: "http://gtfs.org/reference/static/#stoptimestxt" } }, "http://gtfs.org/reference/static/#stoptimestxt"),
                h("p",
                    n.sampleNotices.map(y => h("p", [
                        h("p", { style: { fontWeight: "bold"} }, `trip_id: ${y.tripId}`),
                        h("ul", [
                            h("li", ` stop_sequence=${y.stopSequence} (stop_times.txt:${y.csvRowNumber})`),
                            h("li", `distance ${y.shapeDistTraveled} is <= ${y.prevStopTimeDistTraveled} at stop_sequence=${y.prevStopSequence} (stop_times.txt:${y.prevCsvRowNumber})`)
                        ]) 
                    ]
                )))
            ])
        },

        duplicated_column: n => h("ul",
            n.sampleNotices.map(y => h("li", `"${y.fieldName}" (${y.filename} #${y.firstIndex} is duplicate to #${y.secondIndex})`))),



        duplicate_fare_rule_zone_id_fields: n => {
            let filename = "fare_rules.txt"
            return h("div", [
                h("p", "Rows from \"fare_rules.txtn\" must be unique based on \"fare_rules.route_idn\", \"fare_rules.origin_id\", \"fare_rules.contains_id\" and \"fare_rules.destination_id\""),
                h("ul",
                n.sampleNotices.map(y => h("li", `
                fare_id: ${y.fareId} (${y.filename}:${y.csvRowNumber}) has duplicate zones to fare_id: ${y.previousFareId} (${y.filename}:${y.previousCsvRowNumber})`)))
            ])
        },

        duplicate_key: n => h("ul",
            n.sampleNotices.map(y => h("li", `
                ${y.fieldName1} ${y.fieldValue1} (${y.filename}:${y.oldCsvRowNumber}) is duplicate to ${y.fieldName2} ${y.fieldValue2} (${y.filename}:${y.newCsvRowNumber})`))),

        duplicate_route_name: n => h("div", [
            h("p", "Describes two routes that have the same long and short names, route type and belong to the same agency."),
            h("ul",
                n.sampleNotices.map(y => h("li", 
                    `route_id ${y.routeId1} "${y.routeLongName1}" (routes.txt:${y.csvRowNumber1}) has a duplicate id, short name or long name of route_id ${y.routeId2} "${y.routeLongName2}" (routes.txt:${y.csvRowNumber2})`,
                )))
        ]), 

        empty_column_name: n => h("ul",
            n.sampleNotices.map(y => h("li", `"No column name for (${y.filename} #${y.index})`))),

        empty_row: n => h("ul",
            n.sampleNotices.map(y => h("li", `"No row in (${y.filename}:${y.csvRowNumber}})`))),

        empty_file: n => h("ul",
            n.sampleNotices.map((y) => h("li", `"${y.filename}"`))), 

        fast_travel_between_consecutive_stops: n => h("ul",
                n.sampleNotices.map(y => h("li", [
                    h("p", [
                        h("text", `trip_id: ${y.tripId} route ${y.routeId} (trips.txt:${y.tripCsvRowNumber})`),
                        h("text", `stop_id ${y.stopId1} "${y.stopName1}" stop_sequence=${y.stopSequence1} lat/lon ${y.match1} departed ${y.departureTime1} (stop_times.txt:${y.csvRowNumber1})`),
                        h("text", `arrived ${y.arrivalTime2} stop_id ${y.stopId2} "${y.stopName2}" stop_sequence=${y.stopSequence2} lat/lon ${y.match2} (stop_times.txt:${y.csvRowNumber2})`),
                        h("text", `a distance of ${y.distance}km with a speed of ${y.speedKph}km/h.`)
                    ])
                ]
            ))), 

        fast_travel_between_far_stops: n => h("div", [
            h("p", "Describes a trip where the transit vehicle moves too fast between two far stops."),
            h("p", "This normally indicates a more serious problem than too fast travel between consecutive stops."),
            h("ul",
            n.sampleNotices.map(y => h("li", 
                `trip_id: ${y.tripId} route ${y.routeId} (trips.txt:${y.tripCsvRowNumber}) 
                stop_id ${y.stopId1} "${y.stopName1}" stop_sequence=${y.stopSequence1} lat/lon ${y.match1} departed ${y.departureTime1} (stop_times.txt:${y.csvRowNumber1})
                arrived ${y.arrivalTime2} stop_id ${y.stopId2} "${y.stopName2}" stop_sequence=${y.stopSequence2} lat/lon ${y.match2} (stop_times.txt:${y.csvRowNumber2})
                a distance of ${y.distance}km with a speed of ${y.speedKph}km/h.`,
            ))) 
        ]), 

        feed_expiration_date: n => h("div", [
            h("p", "Validates a feed's expiration date: 1) At any time, the published GTFS dataset should be valid for at least the next 7 days 2) If possible, the GTFS dataset should cover at least the next 30 days of service."),
            h("ul",
            n.sampleNotices.map(y => h("li", `
                Feed ended ${y.feedEndDate}, suggested end date ${y.suggestedExpirationDate}
                (feed_info.txt:#${y.csvRowNumber})`)))
        ]),

        feed_info_lang_and_agency_mismatch: n => h("div", [
            h("p", "agency.agency_lang and feed_info.feed_lang do not match."),
            h("ul",
            n.sampleNotices.map((y) => h("li",
                 `Agency ${y.agencyId} language "${y.agencyLang}" was expected to match 
                feed language "${y.feedLang}" (agency.txt:${y.csvRowNumber})`)))
        ]),
        
        foreign_key_violation: n => h("div", [
            h("p", "Validates that service_id field in \"trips.txt\" references a valid service_id in \"calendar.txt\" or \"calendar_date.txt\""),
            h("ul",
                n.sampleNotices.map((y) => h("li", `
                    "${y.fieldValue}" (${y.parentFilename}:${y.csvRowNumber} @ ${y.parentFieldName}) -> 
                    (${y.childFilename} @ ${y.childFieldName})`)))
        ]),

        inconsistent_agency_lang: n => h("div", [
            h("p", "Inconsistent language among agencies."),
            h("ul",
                n.sampleNotices.map((y) => h("li",
                    `Language ${y.actual}" was expected to be "${y.expected}" (agency.txt:${y.csvRowNumber})`)))
        ]),

        inconsistent_agency_timezone: n => h("div", [
            h("p", "Inconsistent timezone among agencies."),
            h("ul",
            n.sampleNotices.map((y) => h("li",
                 `Time zone "${y.actual}" was expected to be "${y.expected}" (agency.txt:${y.csvRowNumber})`)))
        ]),

        invalid_color: NoticeDetailsGenericFieldValueCodeView,
        invalid_currency: NoticeDetailsGenericFieldValueCodeView,
        invalid_date: NoticeDetailsGenericFieldValueCodeView,
        invalid_email: NoticeDetailsGenericFieldValueCodeView,
        invalid_float: NoticeDetailsGenericFieldValueCodeView,
        invalid_integer: NoticeDetailsGenericFieldValueCodeView,
        invalid_language_code: NoticeDetailsGenericFieldValueCodeView,
        invalid_phone_number: NoticeDetailsGenericFieldValueCodeView,

        invalid_row_length: n => h("ul",
            n.sampleNotices.map(y => h("li", 
                `Length: "${y.rowLength}" (${y.filename}:${y.csvRowNumber} #${y.headerCount}) `,
            ))), 

        invalid_time: NoticeDetailsGenericFieldValueCodeView,
        invalid_timezone: NoticeDetailsGenericFieldValueCodeView,
        invalid_url: NoticeDetailsGenericFieldValueCodeView,

        i_o_error: n => h("ul",
            n.sampleNotices.map(y => h("li", 
                `exception: "${y.exception}" message: "${y.message}"`,
            ))),

        leading_or_trailing_whitespaces: NoticeDetailsGenericFieldValueCodeView,

        location_with_unexpected_stop_time: n => h("div", [
            h("p", "Describes a location in stops.txt that is not a stop but has a stop time associated."),
            h("ul",
            n.sampleNotices.map(y => h("li", 
                `stop_id: ${y.stopId} "${y.stopName}" (stops.txt:${y.csvRowNumber}) and (stop_times.txt${y.stopTimeCsvRowNumber})`,
            )))
        ]),

        location_without_parent_station: n => h("div", [
            h("p", "A location that must have `parent_station` field does not have it."),
            h("p", "The following location types must have `parent_station`: entrance, generic node, boarding area."),
            h("ul",
            n.sampleNotices.map(y => h("li", 
                `stop_id: ${y.stopId} "${y.stopName}" location_type: ${y.locationType} (stops.txt:${y.csvRowNumber})`,
            )))
        ]),
        

        missing_calendar_and_calendar_date_files: n => h("p", "Both the calendar.txt and calendar_dates.txt files are missing."),

        missing_feed_info_date: n => h("div", [
            h("p", "Even though `feed_info.start_date` and `feed_info.end_date` are optional, if one field is provided the second one should also be provided."),
            h("ul",
            n.sampleNotices.map(y => h("li", 
                `column: ${y.fieldName} (feed_info.txt:${y.csvRowNumber}) `,
            )))
        ]),

        missing_level_id: n => h("div", [
            h("p", "A row from stops.txt is linked to a row from pathways.txt with pathways.pathway_mode=5 but has no value for stops.level_id."),
            h("ul",
            n.sampleNotices.map(y => h("li", `stop_id: ${y.stopId} (levels.txt:${y.csvRowNumber})`)))
        ]), 
        

        missing_required_column: n => h("ul",
            n.sampleNotices.map(y => h("li", 
                `column: ${y.fieldName} (${y.filename}) `,
            ))), 

        missing_required_field: n => h("ul",
            n.sampleNotices.map(y => h("li", 
                `column: ${y.fieldName} (${y.filename}:${y.csvRowNumber}) `,
            ))), 

        missing_required_file:  n => h("ul",
            n.sampleNotices.map(y => h("li", 
                `column: ${y.fieldName} (${y.filename}:${y.csvRowNumber}) `,
            ))), 

        missing_timepoint_column: n => h("div", [
            h("p", "Column stop_times.timepoint is missing."),
            h("ul",
            n.sampleNotices.map(y => h("li", `${y.filename}`,)))
        ]),
        
        missing_timepoint_value: n => h("ul",
            n.sampleNotices.map(y => h("li", `trip_id ${y.tripId} stop_sequence=${y.stopSequence} (stop_times.txt:${y.csvRowNumber})`,))),

        missing_trip_edge: n => h("div", [
            h("p", "First and last stop of a trip must define both `arrival_time` and `departure_time` fields."),
            h("p", "If there are not separate times for arrival and departure at a stop, enter the same value for arrival_time and departure_time."),
            h("ul",
            n.sampleNotices.map(y => h("li", 
                `trip_id: ${y.tripId} - stop_sequence=${y.stopSequence} (${y.filename}:${y.csvRowNumber})`,
            )))
        ]), 

        more_than_one_entity: n => h("ul",
            n.sampleNotices.map(y => h("li", `Found ${y.entityCount} entities in (${y.filename})`,))), 

        new_line_in_value: NoticeDetailsGenericFieldValueCodeView,

        number_out_of_range: n => h("ul",
            n.sampleNotices.map(y => h("li", 
                `column: ${y.fieldName} value:"${y.fieldValue}" type ${y.fieldType} (${y.filename}:${y.csvRowNumber}) `,
            ))), 

        non_ascii_or_non_printable_char: NoticeDetailsGenericFieldValueCodeView,

        overlapping_frequency: n => {
            let filename = "frequencies.txt"
            return  h("div", [
                h("p", "Two frequency entries referring to the same trip may not have an overlapping time range."),
                h("p", "Two entries X and Y are considered to directly overlap if <i>X.start_time &lt;= Y.start_time</i> and <i>Y.start_time &lt;X.end_time</i>"),
                h("ul",
                n.sampleNotices.map(y => h("li", `
                    trip_id: ${y.tripId} start time ${y.currStartTime} (${y.filename}:${y.currCsvRowNumber}) 
                    overlaps with end time ${y.prevEndTime} (${y.filename}:${y.prevCsvRowNumber}) 
                    `))) 
            ])
        },

        pathway_dangling_generic_node: n => h("div", [
            h("p", "Describes a dangling generic node, i.e. that has only one incident location in a pathway graph."),
            h("ul",
            n.sampleNotices.map(y => {
                return h("li", 
                    `stop_id ${y.stopId} "${y.stopName}" parent ${y.parentStation} (pathways.txt:${y.csvRowNumber})`,
                )
            }))
        ]), 

        pathway_loop: n => h("div", [
            h("p", "Validates that pathway is not a loop, i.e. it does not start and end at the same location."),
            h("ul",
            n.sampleNotices.map(y => {
                return h("li", 
                    `stop_id ${y.stopId} pathway_id "${y.pathwayId}" (pathways.txt:${y.csvRowNumber})`
                )
            }))
        ]),

        pathway_unreachable_location: n => h("div", [
            h("p", "Describes a location that is not reachable at least in one direction: from the entrances or to the exits."),
            h("ul",
                n.sampleNotices.map(y => {
                    let unreachables = ''
                    if (!!y.hasEntrance && !y.hasExit) {
                        unreachables = "is not reachable from the entrance nor the exit"
                    } else if (!y.hasEntrance) {
                        unreachables = "is not reachable from the entrance"
                    } else if (!y.hasExit) {
                        unreachables = "is not reachable from the exit"
                    }
                    return h("li", 
                        `stop_id ${y.stopId} (type ${y.locationType}) "${y.stopName}" ${unreachables} 
                        from ${y.parentStation} (pathways.txt:${y.csvRowNumber})`
                    )
                }))
        ]),

        platform_without_parent_station: n => h("div", [
            h("p", "A platform has no parent_station field set."),
            h("p", "This is different from LocationWithoutParentStationNotice since it is less severe."),
            h("ul",
            n.sampleNotices.map(y => h("li", 
                `stop_id: ${y.stopId} "${y.stopName}" location_type: ${y.locationType} (stops.txt:${y.csvRowNumber})`,
            )))
        ]),
        

        point_near_origin: n => h("ul",
            n.sampleNotices.map(y => {
                return h("p", [
                    h("li", `Latitude ${y.lonFieldName} ${y.lonFieldValue}`),
                    h("li", `Longitude ${y.latFieldName} ${y.latFieldValue} `),
                    h("li", `(${y.filename}:${y.csvRowNumber})`)
                ])
            })), 

        route_both_short_and_long_name_missing: n => h("div", [
            h("p", "Both `routes.route_short_name` and `routes.route_long_name` are missing for a route."),
            h("ul", 
            n.sampleNotices.map(y => h("li", `(routes.txt:${y.csvRowNumber}) `)))
            ]), 

        route_color_contrast: n => h("div", [
            h("p", "The color difference between route_color and route_text_color should provide sufficient contrast when viewed on a black and white screen. For more information, please visit:"),
            h("a", { props: { href: "http://gtfs.org/best-practices/#routestxt" } }, "http://gtfs.org/best-practices/#routestxt"),
            h("ul", 
                n.sampleNotices.map(y => h("p", [
                    h("li", `route_id: ${y.routeId}`),
                    h("li", `text color "${y.routeTextColor}" & color "${y.routeColor}" (routes.txt:${y.csvRowNumber}) `)
                ])
            ))
            ]), 

        route_short_and_long_name_equal: n => h("div", [
            h("p", "Short and long name are equal for a single route."),
            h("ul", 
            n.sampleNotices.map(y => h("li", `route_id: ${y.routeId} (routes.txt:${y.csvRowNumber}) `)))
            ]),

        route_short_name_too_long: n => h("div", [
            h("p", "Short name of a single route is too long (more than 12 characters. For more information, please visit:"),
            h("a", { props: { href: "http://gtfs.org/best-practices/#routestxt" } }, "http://gtfs.org/best-practices/#routestxt"),
            h("ul", 
            n.sampleNotices.map(y => h("li", `route_id: ${y.routeId} (routes.txt:${y.csvRowNumber}) `)))
            ]), 

        runtime_exception_in_loader_error: n => h("ul",
            n.sampleNotices.map(y => h("li", 
                `exception: "${y.exception}" message: "${y.message}"${y.stopName}" (${y.filename}`,
            ))),

        runtime_exception_in_validator_error: n => h("ul",
            n.sampleNotices.map(y => h("li", 
                `validator "${y.validator}" exception: "${y.exception}" message: "${y.message}"`,
            ))),

        same_name_and_description_for_route: n => h("div", [
            h("p", "A single route has identical values for routes.route_desc and route_long_name or route_short_name."),
            h("ul",
            n.sampleNotices.map(y => h("li", `route_id: ${y.routeId} ${y.specifiedField} (routes.txt:${y.csvRowNumber}) `
            )))
        ]),

        same_name_and_description_for_stop: n => h("div", [
            h("p", "A stop has an identical value in stops.route_desc and stops.stop_name."),
            h("p", "Do not simply duplicate the name of the location. For more informantion, please visit:"),
            h("a", { props: { href: "http://gtfs.org/reference/static#stopstxt" } }, "http://gtfs.org/reference/static#stopstxt"),
            h("ul", 
            n.sampleNotices.map(y => h("li", `stop_id: ${y.stopId} "${y.stopDesc}" (stops.txt:${y.csvRowNumber}) `)))
            ]), 

        same_route_and_agency_url: n => h("div", [
            h("p", "A Route uses the same URL in routes.route_url as has the Agency in agency.txt."),
            h("ul", 
            n.sampleNotices.map(y => h("li", 
                `route_id: ${y.routeId} (routes.txt:${y.routeCsvRowNumber}) agency_id: ${y.agencyId} (agency.txt:${y.agencyCsvRowNumber}) `)))
        ]), 

        same_stop_and_agency_url: n => h("div", [
            h("p", "A Stop uses the same URL in stops.stop_url as uses an Agency in agency.txt."),
            h("ul", 
            n.sampleNotices.map(y => h("li", 
                `stop_id: ${y.stopId} (stops.txt:${y.stopCsvRowNumber}) agency_id: ${y.agencyId} (agency.txt:${y.agencyCsvRowNumber}) `))) 
            ]),

        same_stop_and_route_url: n => h("div", [
            h("p", "A Stop uses the same URL for stops.stop_url as uses a Route in routes.txt."),
            h("ul", 
            n.sampleNotices.map(y => h("li", 
                `stop_id: ${y.stopId} (stops.txt:${y.stopCsvRowNumber}) route_id: ${y.routeId} (route.txt:${y.routeCsvRowNumber}) `)))
            ]), 

        start_and_end_range_equal: n => h("ul", 
            n.sampleNotices.map(y => h("li", 
                `Value ${y.value} is the same in ${y.startFieldName} and ${y.endFieldName} (${y.filename}:${y.csvRowNumber}) `))), 

        start_and_end_range_out_of_order: n => h("ul", 
            n.sampleNotices.map(y => h("li", 
                `Value "${y.value}" of ${y.startFieldName} is out of order with respect to
                value "${y.endValue}" of ${y.endFieldName} for service_id ${y.entityId} (${y.filename}:${y.csvRowNumber}) `))), 

        station_with_parent_station: n => h("div", [
            h("p", "A station has `parent_station` field set."),
            h("ul",
            n.sampleNotices.map(y => h("li", 
                `Station ${y.stopId} "${y.stopName}" has a parent ${y.parentStation} (stops.txt:${y.csvRowNumber})`)))
            ]),

        stop_has_too_many_matches_for_shape: n => h("div", [
            h("p", "Describes a stop entry that has many potential matches to the trip's path of travel, as defined by the shape entry in shapes.txt."),
            h("ul",
            n.sampleNotices.map(y => h("li", 
                `trip_id: ${y.tripId} shape_id ${y.shapeId} (trips.txt:${y.tripCsvRowNumber}) 
                stop_id ${y.stopId} "${y.stopName}" lat/lon ${y.match} maches ${y.matchCount} times (stop_times.txt:${y.stopTimeCsvRowNumber})`,
            )))
        ]),

        stops_match_shape_out_of_order: n => h("div", [
            h("p", "Two stop entries in stop_times.txt appear in a different orderering than their shape would indicate in the shapes.txt file."),
            h("p", "This could indicate a problem with the location of the stops, the path of the shape, or the sequence of the stops for their trip."),
            h("ul",
            n.sampleNotices.map(y => h("li", 
                `trip_id: ${y.tripId} shape_id ${y.shapeId} (trips.txt:${y.tripCsvRowNumber}) 
                stop_id ${y.stopId1} "${y.stopName1}" lat/lon ${y.match1} (stop_times.txt:${y.stopTimeCsvRowNumber1})
                stop_id ${y.stopId2} "${y.stopName2}" lat/lon ${y.match2} (stop_times.txt:${y.stopTimeCsvRowNumber2})`,
            )))
            ]), 

        stop_time_timepoint_without_times: n => h("div", [
            h("p", "Timepoint without a departure time or arrival time."),
            h("ul",
                n.sampleNotices.map(y => h("p", [
                    h("li", { style: { fontWeight: "bold", listStyleType: "none", paddingLeft: 0} }, `trip_id: ${y.tripId}`),
                    h("li", { style: { listStyleType: "none"} }, `stop_sequence=${y.stopSequence} missing ${y.specifiedField} (stop_times.txt:${y.csvRowNumber})`)
                ]))
            )    
        ]), 

        stop_time_with_arrival_before_previous_departure_time: n => h("div", [
            h("p", "Two stop times are out of order."),
            h("p",
                n.sampleNotices.map(y => h("p", [
                    h("p", { style: { fontWeight: "bold"} }, `trip_id: ${y.tripId}`),
                    h("ul", [
                        h("li", `stop_sequence=${y.stopSequence}`),
                        h("li", `arrival ${y.arrivalTime} (stop_times.txt:${y.csvRowNumber})\nis before previous departure ${y.departureTime} (stop_times.txt:${y.prevCsvRowNumber})`)
                    ])
                ]
            )))
        ]),

        stop_time_with_only_arrival_or_departure_time: n =>  h("div", [
            h("p", "Missing stop_times.arrival_time or stop_times.departure_time"),
            h("ul",
            n.sampleNotices.map(y => h("li", 
                `trip_id: ${y.tripId} - stop_sequence=${y.stopSequence} has only ${y.specifiedField} (stop_times.txt:${y.csvRowNumber})`,
            )))
            ]),

        // stop_too_far_from_trip_shape: 
        // Notices.md has this but it's been deprecated per MIGRATION_V2_V3.md

        stop_too_far_from_shape_using_user_distance: n => h("div", [
            h("p", "Describes a stop time entry that is a large distance away from the location of the shape in shapes.txt as defined by shape_dist_traveled values."),
            h("p", "This potentially indicates a problem with the location of the stop or the use of shape_dist_traveled values."),
            h("ul",
            n.sampleNotices.map(y => h("li", 
                `trip_id: ${y.tripId} shape_id ${y.shapeId} (trips.txt:${y.tripCsvRowNumber}) 
                stop_id ${y.stopId} "${y.stopName}" lat/lon ${y.match} is ${y.geoDistanceToShape} from shape (stop_times.txt:${y.stopTimeCsvRowNumber})`,
            )))
        ]), 

        stop_too_far_from_shape: n => h("div", [
            h("p", "Describes a stop time entry that is a large distance away from the trip's path of travel, as defined by the shape entry in shapes.txt."),
            h("p", "This potentially indicates a problem with the location of the stop or the path of the shape."),
            h("ul",
            n.sampleNotices.map(y => h("li", 
                `trip_id: ${y.tripId} shape_id ${y.shapeId} (trips.txt:${y.tripCsvRowNumber}) 
                stop_id ${y.stopId} "${y.stopName}" lat/lon ${y.match} is ${y.geoDistanceToShape} from shape (stop_times.txt:${y.stopTimeCsvRowNumber})`,
            )))
        ]), 

        stop_without_stop_time: n => {
            let filename = "stop_times.txt"
            return h("div", [
                h("p", "Describes a stop that does not have any stop time associated."),
                h("ul",
                n.sampleNotices.map(y => h("li", 
                    `stop_id: ${y.stopId} "${y.stopName}" (${y.filename}:${y.csvRowNumber}) `
                )))
            ])
        },
        
        // too_fast_travel: 
        // Notices.md has this but it's been deprecated per MIGRATION_V2_V3.md

        stop_without_zone_id: n => {
            let filename = "stops.txt"
            return h("div", [
                h("p", "All all stops and platforms (location_type = 0) should have stops.zone_id assigned."),
                h("ul",
                n.sampleNotices.map(y => h("li", 
                    `stop_id: ${y.stopId} "${y.stopName}" (${y.filename}:${y.csvRowNumber}) `,
                )))
            ])
        },

        thread_excecution_error: n => h("ul",
            n.sampleNotices.map(y => h("li", 
                `exception: "${y.exception}" message: "${y.message}"`,
            ))),

        translation_foreign_key_violation: n => h("div", [
            h("p", "An entity with the given (record_id, record_sub_id) cannot be found in the referenced table."),
            h("ul",
            n.sampleNotices.map(y => h("li", 
                `table ${y.tableName} record_id ${y.recordId} record_sub_id ${y.recordSubId} (translations.txt:${y.csvRowNumber}) `,
            )))
        ]), 

        translation_unknown_table_name: n => h("div", [
            h("p", "A translation references an unknown or missing GTFS table."),
            h("ul",
            n.sampleNotices.map(y => h("li", 
                `table name: ${y.tableName} (translations.txt:${y.csvRowNumber}) `,
            )))
            ]), 

        translation_unexpected_value: n => h("div", [
            h("p", "A field in a translations row has value but must be empty."),
            h("ul",
            n.sampleNotices.map(y => h("li", 
                `column: ${y.fieldName} value:"${y.fieldValue}" (translations.txt:${y.csvRowNumber}) `,
            )))
        ]), 

        unexpected_enum_value: n => h("ul",
            n.sampleNotices.map(y => h("li", 
                `column: ${y.fieldName} value:"${y.fieldValue}" (${y.filename}:${y.csvRowNumber}) `,
            ))), 

        unknown_column: n => h("ul",
            n.sampleNotices.map(y => h("li", `"${y.fieldName}" (${y.filename} #${y.index})`))),

        unknown_file: n => h("ul",
            n.sampleNotices.map((y) => h("li", `"${y.filename}"`))), 

        unusable_trip: n => h("div", [
            h("p", "A Trip should be referred to by at least two Stop Times."),
            h("ul",
                n.sampleNotices.map(y => h("li",
                `trip_id: ${y.tripId} (trips.txt:${y.csvRowNumber})`
                )))
            ]),

        unused_shape: n => h("ul",
            n.sampleNotices.map(y => h("li", 
                `shape_id: ${y.shapeId} (shapes.txt:${y.csvRowNumber})`,
            ))), 

        unused_trip: n => h("div", [
            h("p", "Every Trip in trips.txt should be used by some Stop Time in stop_times.txt"),
            h("ul",
            n.sampleNotices.map(y => h("li", 
                `trip_id: ${y.tripId} (trips.txt:${y.csvRowNumber})`
            )))
        ]),

        u_r_i_syntax_error: n => h("ul",
            n.sampleNotices.map(y => h("li", 
                `exception: "${y.exception}" message: "${y.message}"`,
            ))),

        wrong_parent_location_type: n => h("div", [
            h("p", "A platform has no `parent_station` field set."),
            h("ul",
            n.sampleNotices.map(y => h("li", 
                `stop_id: ${y.stopId} "${y.stopName}" type ${y.locationType} (stops.txt:${y.csvRowNumber}) 
                parent stop_id: ${y.parentStation} "${y.parentStopName}" type ${y.parentLocationType} (stops.txt:${y.parentCsvRowNumber})`,
            )))
        ]),

        wrong_stop_time_stop_location_type: n => h("ul",
            n.sampleNotices.map(y => h("li", `trip_id ${y.tripId} - stop_sequence=${y.stopSequence} 
                stop_id: ${y.stopId} type ${y.locationType} (stop_times.txt:${y.csvRowNumber})`,))) 

    },
}

