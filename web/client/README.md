# create-svelte

Everything you need to build a Svelte project, powered by [`create-svelte`](https://github.com/sveltejs/kit/tree/master/packages/create-svelte).

## Creating a project

If you're seeing this, you've probably already done this step. Congrats!

```bash
# create a new project in the current directory
npm create svelte@latest

# create a new project in my-app
npm create svelte@latest my-app
```

## Developing

Once you've created a project and installed dependencies with `npm install` (or `pnpm install` or `yarn`), start a development server:

```bash
npm run dev

# or start the server and open the app in a new browser tab
npm run dev -- --open
```
### Start the web service (from project root directory):

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
