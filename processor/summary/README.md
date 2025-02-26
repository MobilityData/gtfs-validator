# `processor:summary` Module

This module provides an annotation processor for the `@GtfsReportSummary` annotation. It scans the summary class for its fields and extracts their documentation comments to produce a JSON file that describes the summary metadata.

## JSON Structure

The generated JSON is an array where each element represents a field from the summary class. Each field object contains:

- **name:** The field's name.
- **description:** The documentation extracted from the field's Javadoc.
- **type:** A simplified string representation of the field's type.
- **fields (optional):** If the field is a complex type, this property is an array containing nested field objects that follow the same structure.


