git checkout solutions/exercise-1
git merge --ff -m "Merge branch 'main' into solutions/exercise-1" main

git checkout solutions/exercise-2
git merge --ff -m "Merge branch 'solutions/exercise-1' into solutions/exercise-2" solutions/exercise-1

git checkout solutions/exercise-3
git merge --ff -m "Merge branch 'solutions/exercise-2' into solutions/exercise-3" solutions/exercise-2

git checkout solutions/exercise-4
git merge --ff -m "Merge branch 'solutions/exercise-3' into solutions/exercise-4" solutions/exercise-3

git checkout solutions/exercise-5
git merge --ff -m "Merge branch 'solutions/exercise-4' into solutions/exercise-5" solutions/exercise-4

git checkout solutions/exercise-6
git merge --ff -m "Merge branch 'solutions/exercise-5' into solutions/exercise-6" solutions/exercise-5

git checkout solutions/exercise-7
git merge --ff -m "Merge branch 'solutions/exercise-6' into solutions/exercise-7" solutions/exercise-6

git checkout skeleton/exercise-7
git merge --ff -m "Merge branch 'solutions/exercise-7' into skeleton/exercise-7" solutions/exercise-7