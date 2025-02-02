.PHONY: repl test clean deploy install format-check format-fix

SHELL := /bin/bash

repl:
	clojure -M:dev:test:app:repl

test:
	clojure -M:dev:test:app:runner --focus :unit --reporter kaocha.report/documentation --no-capture-output

clean:
	rm -rf target build

lint:
	clojure -M:dev:test:app:clj-kondo --copy-configs --dependencies --parallel --lint "$(shell clojure -A:dev:test -Spath)"
	clojure -M:dev:test:app:clj-kondo --lint "src:test" --fail-level "error"

build:
	clojure -X:jar :sync-pom true :jar "build/hierarchy.jar"

build-uberjar:
	clojure -X:uberjar :sync-pom false :jar "build/hierarchy-test-app.jar"

build-native-config:
	mkdir -p build/graalvm-config
	java -agentlib:native-image-agent=config-output-dir=build/graalvm-config -jar "build/hierarchy-test-app.jar" hierarchy.main

build-native:
	native-image -jar "build/hierarchy-test-app.jar" \
		"-H:+ReportExceptionStackTraces" \
		"-H:+JNI" \
		"-H:EnableURLProtocols=http,https,jar" \
		"-J-Dclojure.spec.skip-macros=true" \
		"-J-Dclojure.compiler.direct-linking=true" \
		"--report-unsupported-elements-at-runtime" \
		"--verbose" \
		"--no-fallback" \
		"--no-server" \
		"--allow-incomplete-classpath" \
		"--trace-object-instantiation=java.lang.Thread" \
		"build/hierarchy-test-app"

deploy: clean build
	clojure -X:deploy-maven

install:
	clojure -X:install-maven

format-check:
	clojure -M:format-check

format-fix:
	clojure -M:format-fix
