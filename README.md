# StatsWS
Oracle OSB stats exporter service

# Build

Needs Java 1.8 and Ant.

The result is a Stats.war file deployable to Oracle WLS with installed OSB. The application must be targeted to the admin server, won't work on managed servers.

# Deployment

Deploy the Stats.war.

Make sure there is a user named `monitor`. Put that user into some non-admin group (`Monitors`?).

# Access the Data

HTTP GET to http:/admin-host-name/Stats

Provide `monitor` credentials.

The response is the stats in CSV format.
