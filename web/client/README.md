# GTFS Web Validator Client

## Developing

Once you've created a project and installed dependencies with `npm install` (or `pnpm install` or `yarn`), start a development server:

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

*This project is powered by [`SvelteKit`](https://kit.svelte.dev/).*
