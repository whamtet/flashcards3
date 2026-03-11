scp matt@flashcards.simpleui.io:flashcards3/flashcards3.db .
scp matt@flashcards.simpleui.io:flashcards3/hours.edn .
rsync -av --ignore-existing matt@flashcards.simpleui.io:~/flashcards3/local .
