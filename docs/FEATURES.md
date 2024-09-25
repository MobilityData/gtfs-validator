# Discovery of features in feeds

The validator will produce the [list of features](https://gtfs.org/getting_started/features/overview/) that it finds in the processed feed according to this table:

| Feature Group              | Feature                   | How is the presence of a feature determined (minimum requirements)  |   Documentation Link                                                                                                                                                                                                                                                                                                                                                                            |
|--------------------------|---------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---|
| Accessibility             | Text-to-speech            | One **tts_stop_name** value in [stops.txt](https://gtfs.org/schedule/reference/#stopstxt)  |   https://gtfs.org/getting_started/features/accessibility/#text-to-speech |
| Accessibility             | Stops Wheelchair Accessibility   | One **wheelchair_boarding** value in [stops.txt](https://gtfs.org/documentation/schedule/reference/#stopstxt) |  https://gtfs.org/getting_started/features/accessibility/#stops-wheelchair-accessibility |
| Accessibility             | Trips Wheelchair Accessibility   | One **wheelchair_accessible** value in [trips.txt](https://gtfs.org/documentation/schedule/reference/#tripstxt) |   https://gtfs.org/getting_started/features/accessibility/#trips-wheelchair-accessibility |
| Base add-ons            | Route Colors              | One **color** value <br>OR<br>one text_color value in [routes.txt](https://gtfs.org/schedule/reference/#routestxt)    |    https://gtfs.org/getting_started/features/base_add-ons/#route-colors |
| Base add-ons            | Bike Allowed              | One **bikes_allowed** value in [trips.txt](https://gtfs.org/schedule/reference/#tripstxt)  |  https://gtfs.org/getting_started/features/base_add-ons/#bike-allowed|
| Base add-ons           | Translations              | One line of data in [translations.txt](https://gtfs.org/schedule/reference/#translationstxt)  |  https://gtfs.org/getting_started/features/base_add-ons/#translations |
| Base add-ons             | Headsigns                 | One **trip_headsign** in [trips.txt](https://gtfs.org/schedule/reference/#tripstxt)<br>OR<br>one stop_headsign value in [stop_times.txt](https://gtfs.org/schedule/reference/#stop_timestxt)  | https://gtfs.org/getting_started/features/base_add-ons/#headsigns      |       
| Base add-ons                | Location types            | One **location_type** value in [stops.txt](https://gtfs.org/schedule/reference/#stopstxt)    | https://gtfs.org/getting_started/features/base_add-ons/#location-types |
| Base add-ons                | Feed Information          | One line of data in [feed_info.txt](https://gtfs.org/schedule/reference/#feed_infotxt)  |  https://gtfs.org/getting_started/features/base_add-ons/#feed-information |
| Base add-ons                | Attributions              | One line of data in [attributions.txt](https://gtfs.org/schedule/reference/#attributionstxt) |   https://gtfs.org/getting_started/features/base_add-ons/#attributions|
| Base add-ons                  | Shapes                    | One line of data in [shapes.txt](https://gtfs.org/schedule/reference/#shapestxt)      |   https://gtfs.org/getting_started/features/base_add-ons/#shapes |   
| Base add-ons               | Transfers                 | One line of data in [transfers.txt](https://gtfs.org/schedule/reference/#transferstxt)     | https://gtfs.org/getting_started/features/base_add-ons/#transfers  |
| Base add-ons | Frequency-Based Service | One line of data in [frequencies.txt](https://gtfs.org/schedule/reference/#frequenciestxt)    | https://gtfs.org/getting_started/features/base_add-ons/#frequency-based-service  |
| Fares                    | Fare Products             | One line of data in [fare_products.txt](https://gtfs.org/schedule/reference/#fare_productstxt)  | https://gtfs.org/getting_started/features/fares/#fare-products |                                                                                                                      
| Fares                    | Fare Media                | One line of data in [fare_media.txt](https://gtfs.org/schedule/reference/#fare_mediatxt) |  https://gtfs.org/getting_started/features/fares/#fare-media |
| Fares                    | Route-Based Fares         | One **network_id** value in [routes.txt](https://gtfs.org/schedule/reference/#routestxt)<br/>OR<br/>one line of data in [networks..txt](https://gtfs.org/schedule/reference/#networkstxt)   |    https://gtfs.org/getting_started/features/fares/#route-based-fares   |
| Fares                    | Time-Based Fares          | One line of data in [timeframes.txt](https://gtfs.org/schedule/reference/#timeframestxt)   | https://gtfs.org/getting_started/features/fares/#time-based-fares  |
| Fares                    | Zone-Based Fares          | One line of data in [areas.txt](https://gtfs.org/schedule/reference/#areastxt)   |     https://gtfs.org/getting_started/features/fares/#zone-based-fares |
| Fares                    | Fares Transfers           | One line of data in [fare_transfer_rules.txt](https://gtfs.org/schedule/reference/#fare_transfer_rulestxt)    |    https://gtfs.org/getting_started/features/fares/#fares-transfers |
| Fares                    | Fares V1                  | One line of data in [fare_attributes.txt](https://gtfs.org/schedule/reference/#fare_attributestxt)   |   https://gtfs.org/getting_started/features/fares/#fares-v1 |
| Pathways                 | Pathways Connections*         | One line of data in [pathways.txt](https://gtfs.org/schedule/reference/#pathwaystxt)     |  https://gtfs.org/getting_started/features/pathways/#pathway-connections |
| Pathways                 | Pathways Details*         | One value of **max_slope**<br/>OR<br/>**min_width** <br/>OR<br/>**length** <br/>OR<br/>**stair_count** in [pathways.txt](https://gtfs.org/schedule/reference/#pathwaystxt) | https://gtfs.org/getting_started/features/pathways/#pathway-details |
| Pathways                 | Levels                    | One line of data in [levels.txt](https://gtfs.org/schedule/reference/#levelstxt)     |   https://gtfs.org/getting_started/features/pathways/#levels  |
| Pathways                 | In-station traversal time | One **traversal_time** value in [pathways.txt](https://gtfs.org/schedule/reference/#pathwaystxt)   |   https://gtfs.org/getting_started/features/pathways/#in-station-traversal-time                                                                                                                                                                                                                                                                                                                                            |
| Pathways                 | Pathway Signs       | One **signposted_as** value<br/>AND<br/>one **reversed_signposted_as** in [pathways.txt](https://gtfs.org/schedule/reference/#pathwaystxt) |       https://gtfs.org/getting_started/features/pathways/#pathway-signs     |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
| Flexible Services        | Continuous Stops          | One **continuous_dropoff** value in [routes.txt](https://gtfs.org/schedule/reference/#routestxt)<br/>OR<br/>one **continuous_pickup** value in [routes.txt](https://gtfs.org/schedule/reference/#routestxt)<br/>OR<br/>one **continuous_dropoff** value in [stop_times.txt](https://gtfs.org/schedule/reference/#stop_timestxt)<br/>OR<br/>one **continuous_pickup** value in [stop_times.txt](https://gtfs.org/schedule/reference/#stop_timestxt) |https://gtfs.org/getting_started/features/flexible_services/#continuous-stop |
| Flexible Services        | Booking Rules          | One record in [booking_rules.txt](https://gtfs.org/documentation/schedule/reference/#booking_rulestxt) |https://gtfs.org/getting_started/features/flexible_services/#booking-rules|
| Flexible Services        | Fixed-Stops Demand Responsive Services         | One record in [location_groups.txt](https://gtfs.org/documentation/schedule/reference/#location_groupstxt) |https://gtfs.org/getting_started/features/flexible_services/#fixed-stops-demand-responsive-services|
| Flexible Services        | Zone-Based Demand Responsive Services         | At least one trip in [stop_times.txt](https://gtfs.org/documentation/schedule/reference/#stop_timestxt) references only **location_id**|https://gtfs.org/getting_started/features/flexible_services/#zone-based-demand-responsive-services|
| Flexible Services        | Predefined Routes with Deviation        | At least one trip in [stop_times.txt](https://gtfs.org/documentation/schedule/reference/#stop_timestxt) references **location_id** AND **stop_id** AND **arrival_time** AND **departure_time**|https://gtfs.org/getting_started/features/flexible_services/#predefined-routes-with-deviation|

# Feature migration

Overview of the list of features for each release. Only the releases that affect the list of features are included in this table: if a release number isn't in this table, it means the features were not affected.  
If a feature is dropped from one older release to a newer release; it means it has been replaced by one or more new features.


| 6.0 | 5.0 | 4.2 | 
|-----|-----|-----|
|Text-to-Speech|Text-to-Speech|Text-to-Speech|
||Wheelchair accessibility|Wheelchair accessibility|
|Stops Wheelchair Accessibility||
|Trips Wheelchair Accessibility||
|Route Colors|Route Colors|Route Colors|
|Bike Allowed||Bike Allowed|Bike Allowed|
|Translations|Translations|Translations|
|Headsigns|Headsigns|Headsigns|
|Fare Products|Fare Products|Fare Products|
|Fare Media|Fare Media|Fare Media|
|Route-Based Fares|Route-Based Fares||
|Time-Based Fares|Time-Based Fares||
|Zone-Based Fares|Zone-Based Fares|Zone-Based Fares|
|Fares Transfers|Transfer Fares||
|Fares V1|Fares V1|Fares V1|
|Pathway Connections|Pathways (basic)* |Pathways|
|Pathway Details|Pathways (extra)* ||
|Levels|Levels||
|In-station traversal time|In-station traversal time|In-station traversal time|
|Pathway Signs|Pathways directions||
|Location types|Location types|Location types|
|Feed Information|Feed Information|Feed Information|
|Attributions|Attributions|Attributions|
|Continuous Stops|Continuous Stops|Continuous Stops|
|Shapes|Shapes|Shapes|
|Transfers|Transfers|Transfers|
|Frequencies|Frequencies|Frequency-Based Trip|
|||Route Names|
|||Agency Information|

