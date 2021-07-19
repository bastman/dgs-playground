# dgs-playground

see: 
- https://github.com/Netflix/dgs-examples-kotlin
-https://piotrminkowski.com/2021/04/08/an-advanced-graphql-with-spring-boot-and-netflix-dgs/
  



http://localhost:8080/graphiql

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


