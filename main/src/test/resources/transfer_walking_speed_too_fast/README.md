# Transfer walking speed test feed

This small GTFS feed tests the `transfer_walking_speed_too_fast` rule using a local input directory.

Stop A is at `(0.0, 0.0)` and stop B is at `(0.0, 0.001)`, approximately 111.195 metres apart by straight-line distance. At 2 metres per second, walking between them takes about 55.6 seconds.

In `feed/transfers.txt`, CSV row 2 (`A,B,2,30`) should produce one INFO notice with `csvRowNumber=2`, `fromStopId=A`, `toStopId=B`, `minTransferTime=30`, and a `stopDistance` of approximately 111.195 metres. CSV row 3 (`B,A,2,60`) should not produce this notice because 60 seconds is sufficient.

The test only checks this rule's straight-line walking-speed heuristic. Other GTFS validation notices may be produced by the full runner and should be assessed separately. The sample service calendar covers 2026-01-01 through 2026-12-31, including the fixed validation date of 2026-10-21.
