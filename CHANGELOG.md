# Change Log
All notable changes to this project will be documented in this file.

## [Unreleased]

## [v0.3.2]
* Bump dictzip@0.12.2

## [v0.3.1]
* Fix Null-Pointer exception when there are multiple articles in single
  entry with certain condition.(#11)
* Cache expiry from last access not write(#13) 
* Bump versions
  * spotless@6.3.0
  * spotbugs@5.0.6
  * actions/setup-java@v3
  * dictzip@0.12.1
* Add test case with proprietary data
  * commit without the data

## [v0.3.0]
* Introduce `StarDictDictionary#loadDictionary` builder utility method.
* Hide `StarDictLoader#load` method.
* Don't search lowercase automatically.
* Add cache mechanism for articles.

## [v0.2.0]
* Change class names

## [v0.1.1]
* Fix javadoc
 
## v0.1.0
* First internal release

[Unreleased]: https://github.com/eb4j/stardict4j/compare/v0.3.2...HEAD
[v0.3.2]: https://github.com/eb4j/stardict4j/compare/v0.3.1...v0.3.2
[v0.3.1]: https://github.com/eb4j/stardict4j/compare/v0.3.0...v0.3.1
[v0.3.0]: https://github.com/eb4j/stardict4j/compare/v0.2.0...v0.3.0
[v0.2.0]: https://github.com/eb4j/stardict4j/compare/v0.1.1...v0.2.0
[v0.1.1]: https://github.com/eb4j/stardict4j/compare/v0.1.0...v0.1.1
