git checkout solutions/exercise-1
git merge --ff -m "Merge commit" main

git checkout solutions/exercise-2
git merge --ff -m "Merge commit" solutions/exercise-1

git checkout solutions/exercise-3
git merge --ff -m "Merge commit" solutions/exercise-2

git checkout solutions/exercise-4
git merge --ff -m "Merge commit" solutions/exercise-3

git checkout solutions/exercise-5
git merge --ff -m "Merge commit" solutions/exercise-4

git checkout solutions/exercise-6
git merge --ff -m "Merge commit" solutions/exercise-5

git checkout solutions/exercise-7
git merge --ff -m "Merge commit" solutions/exercise-6

git checkout skeleton/exercise-7
git merge --ff -m "Merge commit" solutions/exercise-7