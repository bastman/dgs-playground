# dgs-playground

see: 
- https://github.com/Netflix/dgs-examples-java
- https://github.com/Netflix/dgs-examples-kotlin
- https://piotrminkowski.com/2021/04/08/an-advanced-graphql-with-spring-boot-and-netflix-dgs/
- https://github.com/hantsy/spring-graphql-sample  
- https://www.graphql-java.com/blog/threads/
- https://netflix.github.io/dgs/advanced/instrumentation/#graphql-query-complexity

http://localhost:8080/graphiql

## findings

- transaction management: it creates a new transaction for each query instead of joining the current transaction
- queries are slow (e.g.: p95 386ms at concurrency>10)
- dataloaders execute code as async futures in common fork join pool.
  you may want to execute blocking calls to db within a dedicated "IO" thread pool instead
  see: https://www.graphql-java.com/blog/threads/
  
## runbook

### start a  local db
```
$ make db-local.up

```


### example queries + mutations

- [example.query.graphqls](app/src/main/resources/example.query.graphqls)
- [example.mutation.graphqls](app/src/main/resources/example.mutation.graphqls)


## benchmarking

```
# verbose
$ ab -v 4 -n 1 -c 1 -T application/json    -p my_data.json  http://localhost:8080/graphql

# quiet
$ ab -n 1 -c 1 -T application/json    -p my_data.json  http://localhost:8080/graphql

```

file: my_data.json
```
{"query":"query {\n  \n      shows(titleFilter:\"foo\") {\n        __typename\n        showId\n        title\n        releaseYear\n        reviews {\n            __typename\n            reviewId\n            starScore\n            comment\n            submittedDate\n        }\n    }\n  \n}\n\n\n","variables":null}
```
