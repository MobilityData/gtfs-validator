# Instructions to download the latest snapshot version of the validator

The following workflows produce artifacts that are stored for a period of 90 days:

More particularly, [`test_pack_doc.yml`](../.github/workflows/test_pack_doc.yml) is responsible to automatically test, package and document the project.
This workflow is triggered on each commit of each branch. 

As a result, the latest snapshot version of the validator can be found under the list of artifacts generated when this workflow is executed on the `master` branch.
The application jar is named as "Application - cli executable.jar --v-master-sha-*-SNAPSHOT".

See the following instructions to download the artifact:

1. Access the actions listing on the project's main page
1. Select `Test Package Document` in the `Workflows` column
1. Select `master` branch
![access actions](https://user-images.githubusercontent.com/35747326/122930141-b351a100-d339-11eb-9484-16201bbea5c0.png)
These three aforementioned steps can be skipped if you directly go to the following url: [Test Package Document workflow executions on master branch](https://github.com/MobilityData/gtfs-validator/actions/workflows/test_pack_doc.yml?query=branch%3Amaster)

1. Select the first item in the list: it is the latest iteration of the workflow that was run on the master branch
1. Click on the artifact's name that is needed to start download 
![download artifacts](https://user-images.githubusercontent.com/35747326/122931339-cb75f000-d33a-11eb-8089-a7640966a1ef.png)
