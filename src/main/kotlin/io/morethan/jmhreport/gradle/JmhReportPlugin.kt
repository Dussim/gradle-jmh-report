/**
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.morethan.jmhreport.gradle

import io.morethan.jmhreport.gradle.task.JmhReportTask
import me.champeau.jmh.JMHTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register

private const val EXTENSION: String = "jmhReport"

class JmhReportPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit =
        with(project) {
            val extension = extensions.create<JmhReportExtension>(EXTENSION)

            extension.jmhReportOutput.convention(layout.buildDirectory.dir("reports/jmh"))

            pluginManager.withPlugin("me.champeau.jmh") {
                extension.jmhResultPath.convention(
                    tasks.named<JMHTask>("jmh").flatMap { it.resultsFile },
                )
            }

            tasks.register<JmhReportTask>("jmhReport") {
                jmhResultPath = extension.jmhResultPath
                jmhReportOutput = extension.jmhReportOutput
            }
        }
}
