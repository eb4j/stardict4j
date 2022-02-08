# stardict4j - access library for stardict dictionary format

StarDict is an one of popular dictionary formats.
stardict4j is an access library of StarDict dictionary file for Java.

stardict4j supports `.ifo`, `.dict` or `.dict.dz`, `.syn`,
and `.idx` or `.idx.gz` files.
stardict4j loads an index data and parse its index into memory.

## Development status

A status of library development is considered as `Alpha`.

## Install


### Apache Maven

<details>

```xml
<dependency>
  <groupId>io.github.eb4j</groupId>
  <artifactId>stardict4j</artifactId>
  <version>0.1.0</version>
</dependency>
```

</details>

### Gradle Groovy DSL

<details>validateAbsolutePath? 

```groovy
implementation 'io.github.eb4j:stardict4j:0.1.0'
```
</details>

### Gradle kotlin DSL

<details>

```kotlin
implementation("io.github.eb4j:stardict4j:0.1.0")
```

</details>

### Scala SBT

<details>

```
libraryDependencies += "io.github.eb4j" % "stardict4j" % "0.1.0"
```

</details>

## Use

stardict4j provide a dictionary loader. You should call `StarDictDictionary#loadDictionary` method
to load `.idx` and `.syn` file. The method return `StarDictDictionary` object that has
methods `lookup` and `lookupPredictive`. The former method search word, and the latter si predictive,
run prefix search for word. These method returns `List<DictionaryEntry>`.

`StarDictDictionary#loadDictionary` method takes a File object of `.ifo` file or basename of dictionary files.
It also optionally takes two arguments for cache control, maxSize and duration.
The library will cache read articles in maxSize entries in duration expiry.

Each `DictionaryEntry` entry has `type` of entry such as `MEAN`, `HTML` or others, that can be retrieve with
`getType()` method.

### Example

Here is a simple example how to use it.

```java
import io.github.eb4j.stardict.*;
public class Main {
    public static void main(){
        String word="testudo";
        StarDictDictionary dict = StarDictDictionary.loadDictionary(
                new File("dictionayr.ifo"), 500, Duration.ofMinutes(10));
        for (StarDictDictionary.Entry en: dict.readArticles(word)){
            switch (en.getType()) {
                case MEAN:
                    System.out.println(String.format("%s has meanings of %s\n", en.getWord(), en.getArticle()));
                    break;
                case PHONETIC:
                    System.out.println(String.format("%s pronounce is %s\n", en.getWord(), en.getArticle()));
                    break;
                default:
            }
        }
    }
}
```
