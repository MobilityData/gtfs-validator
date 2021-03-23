## Reproducing errors

Have you encountered a problem when validating a GTFS dataset? The best way for us to troubleshoot this is to reproduce the problem. Please see the steps below for how you can create a "pull request" that will run the validator in the GitHub Action environment where we can look at the results together.

1. **Navigate** to `.github/workflows/end_to_end.yml`
1. **Click** the *crayon* icon to enter edit mode ![crayon](https://user-images.githubusercontent.com/35747326/110543436-51f22300-80f8-11eb-8b0e-80a5a1c59510.png)
1. Edit the file `.github/workflows/end_to_end.yml` following instructions on lines 5, 43-45 and **push** on your PR branch (see detailed instructions [here](/docs/REPRODUCE_ERRORS.md#create-a-pull-request-pr))
1. Name your branch from the agency/authority/publisher of the feed you are testing
![pr-creation](https://user-images.githubusercontent.com/35747326/110543965-01c79080-80f9-11eb-8062-746419a6a2ba.png)

You should now see the workflow `End to end / run-on-data` start automatically in your PR checks, running the validator on the dataset you just added. The validation report is collected as a run artifact in the Actions tab of your fork repository on GitHub.

If the workflow run crashes or something doesn't look right in the validation report json file, please open a PR in our repository describing the problem witnessed following the [bug report format](../.github/ISSUE_TEMPLATE/bug_report.md).

🎉 Thank you very much! The end to end workflow will run on the newly created PR in our repository and automatically collect all relevant information. We will automatically be informed of the newly created PR and will follow up directly in the PR.

While we welcome all contributions, our [members and sponsors](https://mobilitydata.org/members/) see their PRs and issues prioritized.
