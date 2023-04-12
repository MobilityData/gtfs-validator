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

## Run It

### Start the web service (from project root directory):

```bash
./gradlew bootRun
```

### Start the client (from the `web/client` directory):

```bash
npm run dev
```


## Deploying Updates

```bash
gcloud builds submit
```
