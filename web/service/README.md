# GTFS Validator Web Service

## Setup

- Create GCP Account
- Create Google Cloud bucket for uploads
- Create Google Cloud bucket for reports/results

The current instance is currnetly configured with the following:

- Upload Bucket: `gs://gtfs-validator-user-uploads`
- Results Bucket: `gs://gs://gtfs-validator-results`

## [Setup Pub/Sub](https://cloud.google.com/run/docs/triggering/pubsub-push)

- Set default region

```bash
gcloud config set run/region us-east1
```

- Service account for invoker

```bash
gcloud iam service-accounts create invoker-gtfs-web \
   --display-name "Invoker for gtfs web pub/sub"
```

- Grant permissions

```bash
gcloud run services add-iam-policy-binding gtfs-validator-web \
   --member=serviceAccount:invoker-gtfs-web@web-based-gtfs-validator.iam.gserviceaccount.com \
   --role=roles/run.invoker
```

- Create pub/sub topic

```bash
gcloud pubsub topics create GTFS-WEB-VALIDATOR
```

- Create a push subscription and associate it with the service account

```bash
gcloud projects add-iam-policy-binding web-based-gtfs-validator \
     --member=serviceAccount:service-1084949198173@gcp-sa-pubsub.iam.gserviceaccount.com \
     --role=roles/iam.serviceAccountTokenCreator
```

- Create a Pub/Sub subscription with the service account

```bash
gcloud pubsub subscriptions create GTFS-WEB-VALIDATOR-SUBSCRIPTION --topic GTFS-WEB-VALIDATOR \
--ack-deadline=600 \
--push-endpoint=https://gtfs-validator-web-mbzoxaljzq-ue.a.run.app/run-validator \
--push-auth-service-account=invoker-gtfs-web@web-based-gtfs-validator.iam.gserviceaccount.com
```

- Test

```bash
gcloud pubsub topics publish GTFS-WEB-VALIDATOR --message "hello"
```

## [Setup Cloud Storage Notifications](https://cloud.google.com/storage/docs/reporting-changes#command-line

- Apply notification configuration

```bash
gcloud storage buckets notifications create gs://gtfs-validator-user-uploads --topic=GTFS-WEB-VALIDATOR --event-types=OBJECT_FINALIZE
```

## CORS setup for upload bucket

```cors.json
[
  {
    "origin": ["*"],
    "method": ["PUT"],
    "responseHeader": ["content-type", "access-control-allow-origin"],
    "maxAgeSeconds": 3600
  }
]
```

```bash
gcloud storage buckets update gs://gtfs-validator-user-uploads --cors-file=cors.json
```

## CORS setup for reports bucket

```cors.json
[
  {
    "origin": ["*"],
    "method": ["HEAD", "GET"],
    "responseHeader": ["content-type", "access-control-allow-origin"],
    "maxAgeSeconds": 3600
  }
]
```

```bash
gcloud storage buckets update gs://gtfs-validator-results --cors-file=cors.json
```

## Google Cloud Storage Bucket Lifecycle

Both `gtfs-validator-user-uploads` and `gtfs-validator-results` have been configured so that all files older than 30 days are deleted. This can be manually configured via the [Google Cloud Storage Console](https://console.cloud.google.com/storage/browser?project=web-based-gtfs-validator) or via the [CLI](https://cloud.google.com/storage/docs/gsutil/commands/lifecycle).

## Local Development

To start the web service fully integrated with a Google Cloud Platform project a valid Google cloud credentials file is required to start the web service. Either complete the steps above to provision a new Google Cloud project or reach out to the maintainers of the project to be granted access to the production instance credentials.
To start the web service without a Google cloud integration, use the `local` Spring Boot profile. To start the web service using the local profile add the following JVM parameters to the starting script, `-Dspring.profiles.active=local`.

The location of credential key file is located in `web/service/src/main/resources/application.properties`.

Once this is completed, the server can be started by running `./gradlew bootRun` in the root of the project.

**Note**: The `web/service/src/test/resources/web-based-gtfs-validator.json` contains a valid primary key, but it is not tied to a valid Google Cloud Service Account.

### Start the client (from the `web/client` directory):

```bash
npm run dev
```


## Deploying Updates

First [install the gcloud cli](https://cloud.google.com/sdk/docs/install) and [authenticate](https://cloud.google.com/sdk/gcloud/reference/auth/login).

```bash
gcloud builds submit
```

## Open API Spec

:warning: **Subject to change**: This API spec may change at any time. We do not recommend building any production systems that depend on this API directly.

Open API Spec V3 documentation is available at `web/service/open-api-spec.json`.
