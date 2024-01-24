# Branching Strategy for releases

## General Considerations

- Generally the main (or master) branch is the one where current development is happening (we'll use main in this document)
- One objective would be to arrive to a point where the main branch can be released very often (every day?).
  This means the main branch is releasable at all times. I believe this strategy is called **Continuous Delivery**.
- At this point we don't think it's possible to do that until the testing infrastructure has been improved.
- Nevertheless, the branching strategy takes this objective into account.
- To further support this objective, the strategy wil try to minimize the number of long-lasting branches.

## Release numbering
- example 4.3.0
- The first number (4 in this example) is incremented for major changes in the software. What major change mean has to be decided.
- The second number (3 in this example) is incremented for minor changes in the software.
- The third number (0 in this example) is used for maintenance releases.
  A maintenance is created if some bug has to be corrected before the next minor release.
![](BranchingDiagram.png)
## Feature/fix branches

- Feature/fix branch are created for every feature we want to add to the software (or bug we want to fix).
- We will call them feature branches for the rest of this document.
- They stem from main, are usually short-lived and are merged back to main.

## Msintannce branches
- In general we will try not to create long-lived release branches.
- A long-lived release branch will be created only if any merge to main that came after the previous release is deemed not suitable to go in the maintenance release.
- For example the change could be too extensive, or introduce an undue risk in the maintenance release.
  This is true in particular if the testing infrastructure is not sufficient.

## Hotfixes
- A hotfix will generally result in a maintenance release.
- Hotfixes branches are similar to feature branches.
- Where does the hotfix branch stem from:
    - Generally from the commit that was tagged for the latest release.
        - See for example commit A in the diagram.
    - In keeping with the **Continuous delivery** objective, we will allow the hotfix branch to stem from a commit
      downstream of the previous release commit if:
        - we consider that all merges coming from feature branches that happened after the previous release can go in the maintenance release.
            - For example if commit C from the diagram contains minor changes like typos we could decide that it's OK to include it in the maintenance release.
            - In that case the hotfix branch can stem from commit C and be merged back into main in commit F.
            - Commit F is tagged as the maintenance release and the maintenance release is built from there.
    - If a merge on the main branch cannot be included in the maintenance release (See for example commit I) then we have no choice but to create a maintenance branch
        - Create a maintenance branch stemming from the last commit of main that can go in the maintenance release. Commit F in our example.
        - After that, main cannot be merged into the maintenance branch, since it would introduce undesirable commits.
        - Also create a hotfix branch stemming from the same commit.
        - Once fixed, merge the hotfix branch into the maintenance branch. This creates commit K, that should be tagged and used for the maintenance release.
        - Also merge the maintenance branch into main.
            - Do not delete the maintenance branch. It becomes a long-lived branch that can be used for further maintenance releases.
            - Merging into main creates commit L in the diagram. Note that at this point there could be conflicts so it becomes important to merge carefully.
 