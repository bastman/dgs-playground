# dgs-playground

see: 
- https://github.com/Netflix/dgs-examples-java
- https://github.com/Netflix/dgs-examples-kotlin
- https://piotrminkowski.com/2021/04/08/an-advanced-graphql-with-spring-boot-and-netflix-dgs/
- https://github.com/hantsy/spring-graphql-sample  



http://localhost:8080/graphiql

## findings

- transaction management: it creates a new transaction for each query instead of joining the current transaction


## runbook

### start a  local db
```
$ make db-local.up

```


### example queries + mutations

- [example.query.graphqls](app/src/main/resources/example.query.graphqls)
- [example.mutation.graphqls](app/src/main/resources/example.mutation.graphqls)



