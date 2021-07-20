# dgs-playground

see: 
- https://github.com/Netflix/dgs-examples-java
- https://github.com/Netflix/dgs-examples-kotlin
- https://piotrminkowski.com/2021/04/08/an-advanced-graphql-with-spring-boot-and-netflix-dgs/
- https://github.com/hantsy/spring-graphql-sample  



http://localhost:8080/graphiql

## findings

- transaction management is weird: it creates a new transaction for each query instead of joining the current transaction
- mutation with returning nested results leads to n+1

## runbook

### start a  local db
```
$ make db-local.up

```


### example queries
```

{
  
  shows(titleFilter:"zark") {
    id
    title
    releaseYear
    __typename
  
    reviews {
      id
      showId
      username
      starScore
    }
  }
}


{
  reviews {
    id
    showId
    username,
    starScore
  }
}

```

```

mutation {
  addShow( show: {title:"foo", releaseYear: 1989}) {
    id
    title
    releaseYear
  }
}

mutation {
  addReview( review: {showId:2, username:"some user", starScore:3}) {
    id
    showId
    username
    starScore
  }
}
```


