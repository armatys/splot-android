package pl.makenika.splot.plugin;

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.security.CodeSource
import java.util.regex.Pattern

public class SplotPlugin implements Plugin<Project> {
    void apply(Project project) {
        def plugin = project.plugins.findPlugin('android')?:project.plugins.findPlugin('android-library')
        if (!plugin) {
            throw new GradleException('You must apply the Android plugin or the Android library plugin before using the SplotInternalPlugin plugin.')
        }

        Project parentProject = project.parent
        Project splotProject = project.parent.subprojects.find { it.name.equals("splot") }
        if (splotProject == null) {
            throw new GradleException("You must add the Splot to your project's dependencies.")
        }

        project.android {
            sourceSets {
                main.java.srcDir('src/main/lua')
            }

            def luaSourcesPath = "src/main/lua"
            def tlModPatt = Pattern.compile("^.*${luaSourcesPath}/(.*).tl")
            def variants = plugin.class.name.endsWith('.LibraryPlugin') ? libraryVariants : applicationVariants

            variants.all {
                def variantName = name
                def taskName = "splot${variantName.capitalize()}"
                project.task(taskName) {
                    ext.srcFiles = project.fileTree(luaSourcesPath).include('**/*.tl')
                    ext.destDir = new File(project.buildDir, "generated/res/generated/${variantName}/raw")

                    doLast {
                        File extractedArchivePath = new File(project.buildDir, "splot-plugin-jar")
                        String typedLuaDir = new File(extractedArchivePath.toString(), "typedlua")
                        File typedLuaFile = new File(typedLuaDir, "tlc")
                        String userLuaPath = "${project.projectDir}/src/main/lua"

                        if (!extractedArchivePath.exists()) {
                            extractSelf(extractedArchivePath)
                        }

                        destDir.mkdirs()
                        srcFiles.getFiles().each { File file ->
                            String basePath = tlModPatt.matcher(file.absolutePath).replaceAll(/$1/)
                            String modulePath = basePath.replaceAll("/", "_")
                            String outFileName = "${modulePath}.lua"
                            String splotTLPath = "${splotProject.projectDir}/src/main/lua"
                            String luaPath = "${userLuaPath}/?.lua;${userLuaPath}/?/init.lua;${splotTLPath}/?.lua;${splotTLPath}/?/init.lua;${typedLuaDir}/?.lua;${typedLuaDir}/typedlua/?.lua;;"
                            String outPath = "${destDir}/splot_lua_${outFileName}"
                            def compileCommand = [ "/usr/local/bin/luajit", typedLuaFile.absolutePath, "-s", "-o", outPath, "${file.getAbsolutePath()}"]

                            println compileCommand
                            Process compileProcess = compileCommand.execute(["LUA_PATH=${luaPath}"], null)
                            compileProcess.in.eachLine { line -> println line }
                            compileProcess.err.eachLine { line -> println line }
                        }
                    }
                }
                javaCompile.dependsOn(taskName)
            }
        }
    }

    static void extractSelf(File extractedArchivePath) {
        CodeSource src = SplotPlugin.class.getProtectionDomain().getCodeSource()
        AntBuilder ant = new AntBuilder()
        ant.unzip(src: src.getLocation().path, dest: extractedArchivePath.absolutePath, overwrite: "true") {
            patternset() {
                include(name: "typedlua/tlc")
                include(name: "typedlua/typedlua/**/*.tld")
                include(name: "typedlua/typedlua/**/*.lua")
            }
        }
    }
}