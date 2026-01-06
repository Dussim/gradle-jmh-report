package io.morethan.jmhreport

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.zip.ZipInputStream

class ExtensionsTest {
    @Test
    fun `Extract a zip`(
        @TempDir testFolder: File,
    ) {
        val jmhVisualizerZip =
            requireNotNull(javaClass.getResourceAsStream("/jmh-visualizer.zip")) {
                "jmh-visualizer.zip must exist in resources"
            }

        ZipInputStream(jmhVisualizerZip).use { zipStream ->
            zipStream.extract(testFolder)
        }

        assertThat(File(testFolder, "bundle.js")).exists()
        assertThat(File(testFolder, "index.html")).exists()
        assertThat(File(testFolder, "favicons")).exists().isDirectory()
        assertThat(File(testFolder, "favicons/favicon.ico")).exists()
    }
}
