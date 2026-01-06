# FORKED FROM https://github.com/jzillmann/gradle-jmh-report

# Gradle-JMH-Report

**_gradle-jmh-report_** is a [Gradle](http://gradle.org) plugin to manage and visualize the result of
your [JMH](http://openjdk.java.net/projects/code-tools/jmh/) benchmarks.

**Note:** **This plugin is not for executing benchmarks**, its for reporting on already executed benchmarks. There are
multiple ways how you can setup your JMH benchmark execution in Gradle, i've written in
this [blog post](https://blog.morethan.io/jmh-with-gradle-from-easy-to-simple-dc872d57cf7f) about it!

## News

- 2026/01/06 - FORKED 0.10.1 - modernize the plugin so that it can work with the newest Gradle 9.2.1 and configuration
  cache
- 2024/04/19 - 0.9.6 Release - On pair with jmh-visualizer-0.9.6 / Couple of bug fixes
- 2018/10/03 - 0.9.0 Release - On pair with
  jmh-visualizer-0.9.1 / [Couple of smaller features](https://github.com/jzillmann/jmh-visualizer/milestone/6?closed=1)
- 2018/07/29 - 0.8.0 Release - On pair with jmh-visualizer-0.8.2 / Technical update, minor bug fixes
- 2017/10/31 - 0.7.0 Release - On pair with jmh-visualizer-0.7 / Multi Run Support
- 2017/09/04 - 0.6.0 Release - On pair with jmh-visualizer-0.6 / Layout Change
- 2017/07/30 - 0.5.0 Release - On pair with jmh-visualizer-0.5 / Focusing on Benchmarks
- 2017/05/30 - 0.4.0 Release - On pair with jmh-visualizer-0.4 / Report on secondary metrics
- 2017/05/14 - 0.2.0 Release - Integrate https://github.com/jzillmann/jmh-visualizer
- 2016/12/10 - 0.1.0 Release

## Features

- Create a HTML report for one benchmark run

**Wanted:**

- Create a HTML report for multiple benchmark runs
- Archive your benchmark results in a structured way

## Getting Started

- Setup JMH for your project (However you wanne do it, you can use
  a [plugin](https://plugins.gradle.org/plugin/me.champeau.jmh) or a
  simple [do-it-your-self-task](exampleProjects/benchmarks/build.gradle.kts).
- Add the plugin:

```
plugins {
  id("xyz.dussim.jmhreport").version("0.10.1")
}
```

- Configure the plugin:

If you choose to use [plugin](https://plugins.gradle.org/plugin/me.champeau.jmh) you do not need to do anything.
This plugin wires itself to the output of a task from pluginme.champeau.jmh

Otherwise:

```kotlin
jmhReport {
    jmhResultPath = tasks.named<YourTaskType>("yourCustomTask").flatMap { it.resultsFile }
}
```

or simply (but it is better to wire it to task output)

```kotlin
jmhReport {
    jmhResultPath = layout.buildDirectory.file("reports/jmh/result.json") //or another path
}
```

- Build the report
    - Given your benchmarks have been executed and the json result file is present, you can execute
      ```./gradlew jmhReport``` which will build the html report.
    - You can setup a finalizer hook as well: ```tasks.jmh.finalizedBy(tasks.jmhReport)```. With this, every time you
      execute your jmh benchmarks, e.g. with _./gradlew jmh_, the _jmhReport_ task will run at the end.

## This is how it looks

![screenshot](https://cloud.githubusercontent.com/assets/148472/26032319/ace91322-3890-11e7-9d0e-7314020a8953.png)

## Project Build

- Execute the examples: `./gradlew -p exampleProjects/java-benchmarks/ jmh`
- How to publish the Gradle plugin:
    - (Optional) Integrate new version of https://github.com/jzillmann/jmh-visualizer
        - `npm run providedZip`
        - `mv jmh-visualizer.zip ../../eclipse/gradle-jmh-report/src/main/resources/`
    - Increase version in [build file](build.gradle.kts)
    - Update _News_ and _Getting Started_ in _README.md_
    - Test report: `./gradlew jmh -p exampleProjects/java-benchmarks/ -Pinclude=".*QuickBenchmark.*"`
    - Commit & Push
    - tag with
        - `git tag -a $releaseVersion -m "$releaseVersion release"`
        - `git push --tags`
    - Execute: `./gradlew publishPlugins`
