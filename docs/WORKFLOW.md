# GitHub Actions workflows

The repository uses GitHub Actions for continuous integration, validation against GTFS datasets,
packaging, publishing, release tasks, and deployment of the web application.

This page provides an overview of the workflows in `.github/workflows/`. For implementation details,
the workflow YAML files remain the source of truth.

## Continuous integration, validation, packaging and publishing

| Workflow | File | Triggers | Purpose |
| --- | --- | --- | --- |
| Test Package Document | [`test_pack_doc.yml`](../.github/workflows/test_pack_doc.yml) | Push to `master`; PR to `master`; release (`prereleased`, `released`). | Runs the main test matrix, packages the CLI snapshot JAR, and builds Javadoc. |
| Rule acceptance tests | [`acceptance_test.yml`](../.github/workflows/acceptance_test.yml) | PR to `master` (`opened`, `synchronize`, `reopened`, `ready_for_review`); ignores Markdown, web-related paths and other specified files. | For applicable pull requests targeting `master`, builds the pull-request and `master` validator JARs, runs them against GTFS datasets from the Mobility Database, compares their reports, and posts the acceptance-test result on the pull request. See [Acceptance tests](ACCEPTANCE_TESTS.md). |
| End to end | [`end_to_end.yml`](../.github/workflows/end_to_end.yml) | Push to `master`; PR to `master`; manual dispatch. Push and PR events ignore Markdown, Python, web, workflow and other specified paths. | Runs the validator against GTFS datasets and collects validation reports as workflow artifacts. See [Reproducing errors](REPRODUCE_ERRORS.md). |
| Format code | [`formatting.yml`](../.github/workflows/formatting.yml) | PR to `master`; ignores `**.md` and `.github/workflows/**`. | Checks Java formatting for pull requests and reports files that do not satisfy the formatter. |
| Docker image | [`docker.yml`](../.github/workflows/docker.yml) | Push to `master`; release (`prereleased`, `released`). | Runs the Java tests, builds and tests Docker images for `linux/amd64` and `linux/arm64`, and pushes the resulting multi-platform image for `master` pushes and releases. |
| Web client CI | [`web_client_pr.yml`](../.github/workflows/web_client_pr.yml) | PR to `master` affecting `web/client/**` or `.github/workflows/web_client_pr.yml`. | Runs web-client continuous-integration checks. |
| Web client Cypress e2e tests | [`web_client_test.yml`](../.github/workflows/web_client_test.yml) | PR to `master` affecting `web/client/**`. | Runs Cypress end-to-end tests and preserves screenshots or videos as artifacts when applicable. |
| Web service CI | [`web_svc_pr.yml`](../.github/workflows/web_svc_pr.yml) | PR to `master` affecting `web/service/**`. | Runs web-service continuous-integration checks. |
| Package Installers | [`package_installers.yml`](../.github/workflows/package_installers.yml) | Push to `master`; PR to `master`; release (`prereleased`, `released`); manual dispatch. Push and PR events ignore `**.md` and `.github/**`. | Builds CLI and GUI artifacts and platform-specific installers, including on pull requests. On release runs, also uploads installers and installer archives to the GitHub release. |
| Upload Release Assets | [`publish_assets.yml`](../.github/workflows/publish_assets.yml) | Release (`prereleased`, `released`); manual dispatch. | For release runs, generates and uploads the CLI JAR, GUI JAR, `rules.json`, and zipped Javadocs as release assets. Runs the Maven Central publishing task on both release and manual-dispatch runs; publishing requires a non-snapshot version, and final release is completed through the Sonatype portal. |
| Publish Jars Snapshots | [`publish_snapshots.yml`](../.github/workflows/publish_snapshots.yml) | Push to `master` affecting `core/src/main/**`, `main/src/main/**`, `model/src/main/**`, `build.gradle` or `.github/workflows/publish_snapshots.yml`; manual dispatch. | Publishes snapshot JARs for relevant changes to core modules or build configuration. |
| Update NOTICE_MIGRATION.md | [`notice_migration_generation.yml`](../.github/workflows/notice_migration_generation.yml) | Manual dispatch with the required `NEW_VERSION` input. | Manually generates notice-migration information by comparing rule data for the previous and specified releases, then creates a pull request containing the documentation update. See the [release instructions](RELEASE.md). |

For instructions on downloading a CLI snapshot artifact produced by GitHub Actions, see
[Downloading the latest snapshot JAR](DOWNLOAD_SNAPSHOT_JAR.md).

## Web application deployment

| Workflow | File | Triggers | Purpose |
| --- | --- | --- | --- |
| Web client staging CD | [`stg_web_client_merge.yml`](../.github/workflows/stg_web_client_merge.yml) | Push to `master`; manual dispatch. | Deploys the web client to the staging environment. |
| Web service staging CD | [`stg_web_svc_merge.yml`](../.github/workflows/stg_web_svc_merge.yml) | Push to `master`; manual dispatch. | Deploys the web service to the staging environment. |
| Web application release process | [`web_release.yml`](../.github/workflows/web_release.yml) | Release (`released`). | On a released GitHub release, runs the web continuous-deployment task and sends a release notification to `mobility-feed-api`, which re-validates the latest datasets of the Mobility Database with the new version 24 hours later. |

## Repository maintenance

| Workflow | File | Triggers | Purpose |
| --- | --- | --- | --- |
| Reference change issue creation | [`create_issue_if_referencemd_changes.yml`](../.github/workflows/create_issue_if_referencemd_changes.yml) | Daily schedule at midnight UTC. | Runs daily, checks commits affecting `gtfs/spec/en/reference.md` in the Google Transit repository, and creates an issue summarizing detected changes. |

## Common workflow considerations

Some workflows deliberately ignore documentation-only or unrelated file changes. A workflow not
starting for a particular pull request therefore does not necessarily indicate a failure.

Artifacts generated by GitHub Actions are downloaded as archives. Where a workflow uploads a JAR,
extract the downloaded artifact before running the JAR. See
[Downloading the latest snapshot JAR](DOWNLOAD_SNAPSHOT_JAR.md).

When reproducing a validation problem against a particular GTFS feed, use the
[reproducing errors guide](REPRODUCE_ERRORS.md) rather than modifying the general workflow
configuration without a specific reason.

For release-specific workflow steps, follow [the release guide](RELEASE.md).
