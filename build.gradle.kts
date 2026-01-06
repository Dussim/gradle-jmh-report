import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17

plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish").version("2.0.0")
}

dependencies {
    compileOnly("me.champeau.jmh:me.champeau.jmh.gradle.plugin:0.7.3")

    implementation(gradleApi())
    implementation(gradleKotlinDsl())

    testImplementation("org.junit.jupiter:junit-jupiter:6.0.1")
    testImplementation("org.assertj:assertj-core:3.27.6")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

group = "xyz.dussim.jmhreport"
version = "0.10.1"

gradlePlugin {
    website = "https://github.com/Dussim/gradle-jmh-report"
    vcsUrl = "https://github.com/Dussim/gradle-jmh-report"
    plugins {
        register("jmhReportPlugin") {
            id = group.toString()
            implementationClass = "io.morethan.jmhreport.gradle.JmhReportPlugin"

            displayName = "JMH Report plugin"
            tags = listOf("jmh", "report", "visualization")
            description = "A Gradle plugin building a visual report on top of your JMH benchmark results!"
        }
    }
}

publishing {
    repositories {
        maven {
            name = "reposiliteRepositorySnapshots"
            url = uri("https://maven.dussim.xyz/snapshots")

            credentials {
                username = providers.gradleProperty("repoUsername").get()
                password = providers.gradleProperty("repoPassword").get()
            }
        }
    }

    publications.withType<MavenPublication>().configureEach {
        pom {
            name = project.name
            description = project.description ?: project.name
            url = "https://github.com/Dussim/gradle-jmh-report"

            licenses {
                license {
                    name = "Apache License, Version 2.0"
                    url = "https://opensource.org/license/apache-2-0"
                }
            }

            developers {
                developer {
                    id = "dussim"
                    name = "Artur Tuzim"
                }
            }
        }
    }
}

kotlin.compilerOptions {
    jvmTarget = JVM_17
    freeCompilerArgs.add("-Xjdk-release=17")
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 17
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.wrapper {
    gradleVersion = "9.2.1"
    distributionType = Wrapper.DistributionType.ALL
}
