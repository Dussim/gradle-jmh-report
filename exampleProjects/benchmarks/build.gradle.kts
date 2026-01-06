import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Execute with ./gradlew -p exampleProjects/benchmarks/

plugins {
    java
    kotlin("jvm").version("2.3.0")
    kotlin("kapt").version("2.3.0")
    id("xyz.dussim.jmhreport")
}

sourceSets {
    register("jmh") {
        val main = sourceSets.main.get()
        compileClasspath += main.output + main.compileClasspath
        runtimeClasspath += main.output + main.runtimeClasspath
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_1_8
    }
}

dependencies {
    implementation("com.google.guava:guava:33.5.0-jre")
    implementation("com.jakewharton.byteunits:byteunits:0.9.1")

    implementation("org.apache.hadoop:hadoop-common:3.4.2")
    implementation("org.apache.hadoop:hadoop-hdfs:3.4.2")

    implementation("com.yahoo.datasketches:memory:0.8.4")

    implementation("org.assertj:assertj-core:2.3.0")

    "jmhImplementation"("org.openjdk.jmh:jmh-core:1.37")
    "jmhAnnotationProcessor"("org.openjdk.jmh:jmh-generator-annprocess:1.37")
}

val jmh by tasks.registering(JmhTask::class) {
    jmhClasspath = sourceSets.getByName("jmh").runtimeClasspath

    finalizedBy(tasks.jmhReport)
}

jmhReport {
    jmhResultPath = jmh.flatMap { it.resultsFile }
}

tasks.register("jmhHelp") {
    description = "Print help for the jmh task"
    group = "Development"
    doLast {
        println(
            """
            Usage of jmh tasks:
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~
            Only execute specific benchmark(s):
            	gw jmh -Pinclude=".*MyBenchmark.*"

            Specify extra profilers:
            	gw jmh -Pprofilers="gc,stack"

            Prominent profilers:
            	comp - JitCompilations, tune your iterations
            	stack - which methods used most time
            	gc - print garbage collection stats
            	hs_thr - thread usage

            Change report format from JSON to one of [CSV, JSON, NONE, SCSV, TEXT]:
            	gw jmh -Pformat=csv

            Specify JVM arguments:
            	gw jmh -PjvmArgs="-Dtest.cluster=local"

            Resources:
            	https://jenkov.com/tutorials/java-performance/jmh.html (Introduction)
            	https://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/ (Samples)
            """.trimIndent(),
        )
    }
}

/**
 * Task for executing JMH benchmarks.
 */
abstract class JmhTask
    @Inject
    constructor(
        private val providers: ProviderFactory,
        private val layout: ProjectLayout,
    ) : JavaExec() {
        @get:InputFiles
        @get:Classpath
        abstract val jmhClasspath: ConfigurableFileCollection

        @get:Input
        abstract val includes: Property<String>

        @get:Input
        @get:Optional
        abstract val excludes: Property<String>

        @get:Input
        abstract val resultFormat: Property<String>

        @get:Input
        @get:Optional
        abstract val profilers: ListProperty<String>

        @get:Input
        abstract val failOnError: Property<Boolean>

        @get:Input
        abstract val verbosity: Property<String>

        @get:Input
        @get:Optional
        abstract val jvmArgsPrepend: ListProperty<String>

        @get:OutputFile
        abstract val resultsFile: RegularFileProperty

        init {
            group = "Development"
            description = "Execute JMH benchmarks"
            mainClass.set("org.openjdk.jmh.Main")

            // Set conventions from Gradle properties
            includes.convention(providers.gradleProperty("include").orElse(""))
            excludes.convention(providers.gradleProperty("exclude"))
            resultFormat.convention(providers.gradleProperty("format").orElse("json"))
            profilers.convention(providers.gradleProperty("profilers").map { it.split(',') })

            failOnError.convention(true)
            verbosity.convention("NORMAL")

            jvmArgsPrepend.convention(listOf("-Xmx512m", "-Xms512m"))

            resultsFile.convention(
                resultFormat.flatMap { format ->
                    layout.buildDirectory.file("reports/jmh/result.$format")
                },
            )
        }

        @TaskAction
        override fun exec() {
            classpath = jmhClasspath
            val arguments =
                buildList<String> {
                    // Include pattern
                    add(includes.get())

                    // Exclude pattern
                    if (excludes.isPresent) {
                        add("-e")
                        add(excludes.get())
                    }

                    // Fail on error
                    add("-foe")
                    add(failOnError.get().toString())

                    // Verbosity
                    add("-v")
                    add(verbosity.get())

                    // Profilers
                    profilers.orNull?.forEach { profiler ->
                        add("-prof")
                        add(profiler)
                    }

                    jvmArgsPrepend.orNull?.forEach { arg ->
                        add("-jvmArgsPrepend")
                        add(arg)
                    }

                    // Result format and file
                    add("-rf")
                    add(resultFormat.get())
                    add("-rff")
                    add(resultsFile.get().asFile.absolutePath)
                }

            args(arguments)

            println("\nExecuting JMH with: $arguments\n")

            super.exec()
        }
    }
