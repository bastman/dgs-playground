print-%: ; @echo $*=$($*)
guard-%:
	@test ${${*}} || (echo "FAILED! Environment variable $* not set " && exit 1)
	@echo "-> use env var $* = ${${*}}";

.PHONY : help
help : Makefile
	@sed -n 's/^##//p' $<

## db-local.up:   : start postgres
db-local.up:
	cd docker/postgres && docker-compose -f docker-compose.yml up
## db-local.down:   : stop postgres
db-local.down:
	cd docker/postgres && docker-compose -f docker-compose.yml down
## db-local.down.v:   : stop postgres and remove volumes
db-local.down.v:
	cd docker/postgres && docker-compose -f docker-compose.yml down -v
