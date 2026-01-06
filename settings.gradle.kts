pluginManagement {
    repositories.gradlePluginPortal()
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

includeBuild("exampleProjects/benchmarks")

rootProject.name = "gradle-jmh-report"
