clean:
	rm -rf target

run:
	clj -M:dev

repl:
	clj -M:dev:nrepl

test:
	clj -M:test

testrepl:
	clj -M:test:nrepl

uberjar:
	pkill java && npm run tailwind && clj -T:build all

uberjarlight:
	npm run tailwind && clj -T:build all
