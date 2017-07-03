# Crossref Event Data Wordpress.com Agent

Agent for gathering data from sites hosted on wordpress.com, the commercial hosting provider.

Work-in-progress as of June 2016.

## To run

To run as an agent, `lein run`. 

## Tests

### Unit tests

 - `time docker-compose -f docker-compose-unit-tests.yml run -w /usr/src/app test lein test :unit`

## Demo

    time docker-compose -f docker-compose-unit-tests.yml run -w /usr/src/app test lein repl

## Config

Uses Event Data global configuration namespace.

 - `WORDPRESSDOTCOM_JWT`
 - `GLOBAL_ARTIFACT_URL_BASE`
 - `GLOBAL_KAFKA_BOOTSTRAP_SERVERS`
 - `GLOBAL_STATUS_TOPIC`


## License

Copyright © 2016 Crossref

Distributed under the MIT License.
