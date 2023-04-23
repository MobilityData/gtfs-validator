# web pipeline

The web pipeline is a gradle framework to perform build and deploy steps for
the validator web application. Each component (client, service) implements a set
of tasks with standardized names and those tasks are coordinated by the
`:web:pipeline` project.

The web pipeline tasks require inputs from the environment (for deployment
phase tasks) and from gradle properties (for build phase tasks).

## Quick Start

To perform a full build & deploy to production:

```bash
set -o allexport
source web/pipeline/prd.env
./gradlew webCI webCD
```

## Standard Pipeline Tasks

- `webTest`: This task runs tests on the component
- `webBuild`: This task builds the component
- `webDeploy`: This task deploys the component
- `webCI`: This task runs `webTest`, `webBuild` in sequence on all components in
 parallel
- `webCD`: This task runs `webDeploy` on all components in parallel

## Deployment Environment

Deployment tasks should be expected to require environment variable inputs which
describe the deployment target. Such variables for shared environments should be
checked in to the location `web/pipeline/{envName}.env`. One of these
environment files needs to be sourced prior to running any deployment phase
tasks.

## Build Environment & Properties

Some build tasks may also require inputs. These are expected to be provided via
a project's `gradle.properties` file. Where build inputs must be switched
according to a target environment, the convention is to set the environment
variable `WEB_BUILD_ENV` and lookup properties under the key
`env.{WEB_BUILD_ENV}.{propertyName}`.
