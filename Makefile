.PHONY: up down run test build

up:
	docker compose up -d

down:
	docker compose down -v

run:
	./gradlew bootRun --args='--spring.profiles.active=dev'

test:
	./gradlew clean test

build:
	./gradlew clean build
