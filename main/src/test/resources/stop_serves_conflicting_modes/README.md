# Conflicting modes test feed

This feed exercises [MobilityData/gtfs-validator#2215](https://github.com/MobilityData/gtfs-validator/issues/2215).

Expected `stop_serves_conflicting_modes` results:

- `C`: bus and rail share a stop, producing one `WARNING` with `routeIds` equal to `bus, rail` and `routeTypes` equal to `RAIL, BUS`.
- `A`: tram and bus share a stop, which is allowed.
- `M`: monorail and rail share a stop, which is allowed.
- `BUS_BAY` and `RAIL_BAY`: separate stops under the same station. Their modes are evaluated separately.

Each trip has a separate terminal stop. The report test uses 28 October 2026 as its validation date and checks the generated JSON and HTML reports.
