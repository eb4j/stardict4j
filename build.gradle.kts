plugins {
    `java-library`
    checkstyle
    jacoco
    id("com.github.spotbugs") version "5.0.3"
    id("com.diffplug.spotless") version "6.0.5"
    id("com.github.kt3k.coveralls") version "2.12.0"
    id("com.palantir.git-version") version "0.12.3"
}

// we handle cases without .git directory
val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra
val details = versionDetails()
val baseVersion = details.lastTag.substring(1)
if (details.isCleanTag) {  // release version
    version = baseVersion
} else {  // snapshot version
    version = baseVersion + "-" + details.commitDistance + "-" + details.gitHash + "-SNAPSHOT"
}

group = "io.github.eb4j"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("io.github.dictzip:dictzip:0.11.2")
    implementation("com.github.takawitter:trie4j:0.9.8")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

spotbugs {
    // excludeFilter.set(project.file("config/spotbugs/exclude.xml"))
    tasks.spotbugsMain {
        reports.create("html") {
            required.set(true)
        }
    }
    tasks.spotbugsTest {
        reports.create("html") {
            required.set(true)
        }
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

jacoco {
    toolVersion="0.8.6"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required.set(true) // coveralls plugin depends on xml format report
        html.required.set(true)
    }
}

coveralls {
    jacocoReportPath = "build/reports/jacoco/test/jacocoTestReport.xml"
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
    options.compilerArgs.add("-Xlint:unchecked")
}
