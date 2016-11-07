Popular Movies MVP
===============

### Summary
This is a sample app fetching popular movies. This project is based on MVP and uses RxJava alongside with Content provider with loaders for communication between the data model and presentation layer.

The remote data layer uses RxJava `Observable` streams for retrieving movies, trailers and reviews. 

The `MoviesDataSource` contains methods like:

```java
Observable<Movies> getMovies(String sortBy);
Observable<Trailers> getTrailers(String id);
void addToFav(Movie movie);
void removeFromFav(Movie movie);
```

This is implemented in `MoviesLocalDataSource` as well which uses `ContentResolver` to store favorite movies in a content provider. 

```java
 private MoviesLocalDataSource(@NonNull ContentResolver contentResolver) {
        this.contentResolver = checkNotNull(contentResolver, "ContentResolver cannot be null");
    }
```


### Dependecies

* RxJava
* RxAndroid
* Retrofit
* butterknife
* Picasso
* okhttp3
