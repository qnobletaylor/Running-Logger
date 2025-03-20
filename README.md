# Running Activity Log App

![Demo](https://i.imgur.com/bLXuh5y.gif)

This web app simply lets a user input information about a run they wish to track. 

This is meant as more of an exercise into beginner web app development rather than actually offering
a useful application. There are after all many useful athletic tracking apps out there (i.e. strava, garmin, etc.)

I did however add functionality to insert a strava activity ID so that when viewing the data there is
an option for a bit more *flavor* rather than just viewing plain text.

This app uses maven and the jetty plugin. To run, simply use `mvn jetty:run` and then connect through a browser to
[localhost:8080](http://localhost:8080) .

This app also includes an SQLite database for storing all input data from the user. Included below is an example of the
 table stucture used in the app's database.

Please enjoy.

### Valid Addresses

 - `localhost:8080`
 - `localhost:8080/Run`


### SQL Table

```sqlite
CREATE TABLE "RUNS" (
    "RunID"	INTEGER,
    "Date"	TEXT NOT NULL,
    "Distance"	NUMERIC NOT NULL,
    "Time"	NUMERIC NOT NULL,
    "Speed"	GENERATED ALWAYS AS (("Time" / 60) / "Distance") VIRTUAL,
    "StravaID"	TEXT,
    PRIMARY KEY("RunID" AUTOINCREMENT)
);
```