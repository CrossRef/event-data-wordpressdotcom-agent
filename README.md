# Crossref Event Data Wordpress.com Agent

Experimental agent for gathering data from sites hosted on wordpress.com, the commercial hosting provider.

Work-in-progress as of June 2016.

Connects to the Wordpress API once a day and queries for every member domain for yesterday's date. Then uses the DOI Reversal Service to attempt to discover the DOI for each. 

## Running

Create a config file in `config/dev/config.edn` or `config/prod/config.edn` or via environment variables. Keys required specified in `core.clj` plus standard Baleen ones.

### Daily

Daily stashes audit logs on a daily basis. Runs and exits.

    lein with-profile dev run daily

### Queue Domains

Add adily domain job to queue. Must be run once a day. Runs and exits.

    lein with-profile dev run queue-domains

### Process

Processes all request commands with a 5 second pause for rate limiting. Runs continously. 

    lein with-profile dev run process

### Push

Pushes events into Lagotto. Runs continously.

    lein with-profile dev run push

### Monitor

Runs monitoring web server. Runs continously.

    lein with-profile dev run monitor

## License

Copyright © 2016 Crossref

Distributed under the MIT License.
