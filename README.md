# jira-rest-java-client

[![Atlassian license](https://img.shields.io/badge/license-Apache%202.0-blue.svg?style=flat-square)](LICENSE) [![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](CONTRIBUTING.md)

Please see project documentation at https://ecosystem.atlassian.net/wiki/display/JRJC/.

## Usage

In order to start JIRA please execute atlas-debug from test directory.

## Documentation

Please see project documentation at https://ecosystem.atlassian.net/wiki/display/JRJC/.

## Tests

To run a single test class, you can pass -Dit.test=ExamplesTest to maven.

In order to run integration tests on a snapshot cloud version of JIRA run:

mvn clean integration-test -Djira.version=1001.0.0-SNAPSHOT -Pjira-cloud -nsu

To run integrations tests manually, you need to set up JIRA with a running instance of postgres.

To start a pre-setup postgres run

docker run -p 5434:5432 docker.atl-paas.net/jira-cloud/postgres-ci:9.5

and then in another terminal

mvn jira:debug -Ddocker.host.address=<Docker-machine Ip> -Ddatabase.port=5434

you can find your docker machine ip when running docker quickstart.

CI: https://server-gdn-bamboo.internal.atlassian.com/browse/PLUGINS-JRJCTWO

## Contributions

Contributions to jira-test-java-client are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for details.

**IMPORTANT**: please do not touch the rest_api_guard branch - master is being auto-merged to that branch
so there is no need to do any changes there (doing so will cause conflicts).

## License

Copyright (c) 2010-2023 Atlassian US., Inc.
Apache 2.0 licensed, see [LICENSE](LICENSE) file.
