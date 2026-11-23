# Paging KMP

A [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) paging helper for Android and iOS. It loads pages either purely in memory or backed by a local cache with a time-to-live.

Originally developed inside [rick-and-morty-kmp](https://github.com/plusmobileapps/rick-and-morty-kmp).

## Setup

[![Maven Central](https://img.shields.io/maven-central/v/com.plusmobileapps/paging?color=blue)](https://search.maven.org/artifact/com.plusmobileapps/paging)

```kotlin
commonMain.dependencies {
    implementation("com.plusmobileapps:paging:$version")
}
```

## Usage

Implement a `PageLoader` that fetches one page at a time. Return a `null` paging token once the last page is loaded.

```kotlin
class CharacterPageLoader(private val api: Api) : PageLoader<String, Character> {
    override suspend fun load(request: PageLoaderRequest<String>): PageLoaderResponse<Character> =
        try {
            val page = api.search(query = request.input, page = request.pagingKey)
            PageLoaderResponse.Success(data = page.results, pagingToken = page.next)
        } catch (e: Exception) {
            PageLoaderResponse.Error(e)
        }
}
```

### In-memory

```kotlin
val loader = PagingDataSourceFactory.create(CharacterPageLoader(api))

loader.state.collect { state -> /* render state.data, loading flags, errors */ }
loader.clearAndLoadFirstPage("rick")
loader.loadNextPage()
```

### Cached

Hand the loader functions that read and write your local store (SQLDelight, Room, ...). The first page is refetched once `ttl` has elapsed.

```kotlin
val loader = PagingDataSourceFactory.create(
    cacheInfo = CachedPageLoader.CacheInfo(ttl = 30.minutes, cachingKey = "characters"),
    reader = { db.characters() },
    writer = { db.insert(it) },
    deleteAllAndWrite = { db.replaceAll(it) },
    pageLoader = CharacterPageLoader(api),
)
```

## Development

```bash
./gradlew build      # compile and run Android + iOS simulator tests
./gradlew allTests
```

Publishing goes to Maven Central through the [vanniktech maven-publish plugin](https://vanniktech.github.io/gradle-maven-publish-plugin/central/). Set `mavenCentralUsername`, `mavenCentralPassword` and the `signingInMemoryKey*` properties in `~/.gradle/gradle.properties` (or as `ORG_GRADLE_PROJECT_` env vars), then:

```bash
./gradlew publishToMavenCentral
```
