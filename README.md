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

 - `PERCOLATOR_SERVICE` e.g. https://percolator.eventdata.crossref.org
 - `JWT_TOKEN`
 - `STATUS_SERVICE_BASE`
 - `ARTIFACT_BASE`, e.g. https://artifact.eventdata.crossref.org

## License

Copyright © 2016 Crossref

Distributed under the MIT License.
