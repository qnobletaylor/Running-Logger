# SQL Table Creation Statement

```sqlite
CREATE TABLE "RUNS" (
    "RunID"	INTEGER,
    "Date"	TEXT NOT NULL,
    "Distance"	NUMERIC NOT NULL,
    "Time"	NUMERIC NOT NULL,
    "Speed"	GENERATED ALWAYS AS (("Time" / 60) / "Distance") VIRTUAL,
    "GPS"	TEXT,
    PRIMARY KEY("RunID" AUTOINCREMENT)
);
```