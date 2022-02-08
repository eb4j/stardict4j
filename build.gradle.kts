plugins {
    checkstyle
    jacoco
    signing
    `java-library`
    `java-library-distribution`
    `maven-publish`
    id("com.github.spotbugs") version "5.0.5"
    id("com.diffplug.spotless") version "6.2.1"
    id("com.github.kt3k.coveralls") version "2.12.0"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("com.palantir.git-version") version "0.13.0"
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
}

dependencies {
    implementation("io.github.dictzip:dictzip:0.11.2")
    implementation("com.github.takawitter:trie4j:0.9.8")
    implementation("com.github.ben-manes.caffeine:caffeine:3.0.5")
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

tasks.coveralls {
    dependsOn(tasks.jacocoTestReport)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
    options.compilerArgs.add("-Xlint:unchecked")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set("stardict4j")
                description.set("Stardict access library for java")
                url.set("https://github.com/eb4j/stardict4j")
                licenses {
                    license {
                        name.set("The GNU General Public License, Version 3")
                        url.set("https://www.gnu.org/licenses/licenses/gpl-3.html")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("miurahr")
                        name.set("Hiroshi Miura")
                        email.set("miurahr@linux.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/eb4j/stardict4j.git")
                    developerConnection.set("scm:git:git://github.com/eb4j/stardict4j.git")
                    url.set("https://github.com/eb4j/stardict4j")
                }
            }
        }
    }
}

signing {
    if (project.hasProperty("signingKey")) {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
    } else {
        useGpgCmd()
    }
    sign(publishing.publications["mavenJava"])
}

tasks.withType<Sign> {
    val hasKey = project.hasProperty("signingKey") || project.hasProperty("signing.gnupg.keyName")
    onlyIf { hasKey && details.isCleanTag }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(System.getenv("SONATYPE_USER"))
            password.set(System.getenv("SONATYPE_PASS"))
        }
    }
}
