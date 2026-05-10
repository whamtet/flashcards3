scp matt@flashcards.simpleui.io:acastream/flashcards3.db .
scp matt@flashcards.simpleui.io:acastream/hours.edn .
rsync -av --ignore-existing matt@flashcards.simpleui.io:~/acastream/local .
