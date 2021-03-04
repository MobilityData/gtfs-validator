## Create a pull request (PR)
1. **navigate** to `.github/workflows/end_to_end.yml`
1. **click** the *crayon* icon to enter edit mode
1. on line 5, **replace** `transport-agency-name` by something significant like `societe-de-transport-de-montreal` if you were adding a dataset from *STM*
1. **keep it around** as you'll need it in *step 11.*
1. **uncomment** line 43 by removing the `#` character
1. on line 43, **replace** `ACRONYM` by some acronym for the Agency/publisher, in our example that would be `STM`
1. **uncomment** line 44 by removing the `#` character
1. on line 44, **replace** `[[[ACRONYM]]]` in `[[[ACRONYM]]].zip` by what you put down in step 4 - **NO SPACES OR SPECIAL CHARACTERS -- keep the .zip extension intact**
1. on line 44, **replace** `DATASET_PUBLIC_URL` by a *public url* pointing to a [GTFS Schedule](https://gtfs.mobilitydata.org/spec/gtfs-schedule) zip archive
1. **click** on the *green* `Start commit` button on the right of the page
1. **select the option ` Create a new branch for this commit and start a pull request.`**
1. **replace** the proposed default branch name by what you got from *step 7.* Note that the branch name must exactly match the line 5 text (e.g., `societe-de-transport-de-montreal`).
1. **click** the *green* `Propose changes` button
1. on the next screen, **click** `Create pull request`

You should now see the workflow `End to end / run-on-data` start automatically in your PR checks, running the validator on the dataset you just added. The validation report is collected as a run artifact in the Actions tab of your fork repository on GitHub.

🎉 Thank you very very much! The end to end workflow will run on the newly created PR in our repository and automatically collect all relevant information. We take care of everything from then and will follow up directly in the PR.

While we welcome all contributions, our [members and sponsors](https://mobilitydata.org/members/) see their PRs and issues prioritized.
