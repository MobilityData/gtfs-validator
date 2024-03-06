# GTFS Web Validator Client

## Developing

Once you've created a project and installed dependencies with `npm install`, start a development server:

```bash
npm run dev

# or start the server and open the app in a new browser tab
npm run dev -- --open
```
### Use locally running web service (from project root directory):

The project is configured to use the production instance of the web service (https://gtfs-validator-web-mbzoxaljzq-ue.a.run.app).

In order to use a locally running version of the web service, update `apiRoot` in `web/client/src/routes/+page.svelte` to `http://localhost:8080`.

To start the service, run the following in the root of the project.

```bash
./gradlew bootRun
```

## Building

To create a production version of your app:

```bash
npm run build
```

You can preview the production build with `npm run preview`.

## Deploying

This app is currently hosted on Google Cloud Storage. To deploy these updates, first [install](https://cloud.google.com/sdk/docs/install-sdk) and [authenticate](https://cloud.google.com/sdk/gcloud/reference/auth) the `gcloud` command line interface.

1. Build the production version of the app:

```bash
npm run build
```

2. Upload the app to Google Cloud Storage:

```bash
gcloud storage cp --recursive ./build/* gs://gtfs-validator-web/
```

# Component and E2E tests

Component and E2E tests are executed with [Cypress](https://docs.cypress.io/). Cypress tests are located in the _cypress_ folder.

Steps to run cypress tests locally:
 - Open a terminal and start the web application with Cypress profile
 ```
 npm run start:cypress:config
 ```
 - In another terminal, run cypress headless cypress or cypress dashboard:
 headless:
 ```
npm run cypress:run
 ```
dashboard:
 ```
npm run cypress:open
 ```

## Cypress feedback on GitHub actions

Cypress is configured to capture videos of the test executions and get screenshots(on error). To debug Cypress fails on the pipeline, check the Cypress GitHub workflow artifacts for videos and screenshots.

*This project is powered by [`SvelteKit`](https://kit.svelte.dev/).*
