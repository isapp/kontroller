# Releasing

## Snapshots

Snapshots are built and deployed on every PR merge into `master`.

## Release

Release builds are deployed using the following procedure:

1. `git checkout master`
2. `git pull --rebase`
3. `git checkout release`
4. `git rebase master`
5. `git push origin head`

That will kick off a travis build that will deploy release artifacts to Bintray and bump the version.