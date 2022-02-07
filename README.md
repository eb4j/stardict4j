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

stardict4j provide a dictionary loader. You should call `StarDict#loadDict` method to load `.idx` and `.syn` file.
The method return `StarDictDictionary` object that has methods `lookup` and `lookupPredictive`. The former method
search word, and the latter si predictive, run prefix search for word.
These method returns `List<DictionaryEntry>`.

### Example

Here is a simple example how to use it.

```java
import io.github.eb4j.stardict.*;
public class Main {
    public static void main(){
        StarDictDictionary dict=StarDict.loadDict(new File("dictionayr.ifo"));
        String word="testudo";
        List<DictionaryEntry> results=dict.readArticles(word);
        for(DictionaryEntry en:results){
        System.out.println(String.format("%s has meanings of %s\n", en.getWord(), en.getArticle()));
        }
    }
}
```
