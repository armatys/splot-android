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

        Project splotProject = project.parent.subprojects.find { it.name.equals("splot") }
        if (splotProject == null) {
            throw new GradleException("You must add the Splot to your project's dependencies.")
        }

        project.extensions.create("splot", SplotPluginExtension)
        splotProject.extensions.add("splot", project.extensions.getByName("splot"))

        project.android {
            sourceSets {
                main.java.srcDir(project.splot.luaSourcesPath)
            }

            def variants = plugin.class.name.endsWith('.LibraryPlugin') ? libraryVariants : applicationVariants

            variants.all {
                def variantName = name
                def destDir = new File(project.buildDir, "intermediates/assets/${variantName}/splot_lua")
                def taskName = "splot${variantName.capitalize()}"

                project.task(taskName) {
                    def luaSources = project.splot.luaSourcesPath
                    def tlModPatt = Pattern.compile("^.*${luaSources}/(.*).tl")
                    def srcFiles = project.fileTree(luaSources).include('**/*.tl')

                    inputs.file srcFiles
                    outputs.dir destDir

                    doLast {
                        compileLuaFiles(splotProject, project, destDir, (String)project.splot.luajitPath, srcFiles, tlModPatt)
                    }
                }
                preBuild.dependsOn(taskName)
            }
        }
    }

    static void compileLuaFiles(Project splotProject, Project project, File destDir, String luajitPath, def srcFiles, Pattern tlModPatt) {
        File extractedArchivePath = new File(project.buildDir, "splot-plugin-jar")
        String typedLuaDir = new File(extractedArchivePath.toString(), "typedlua")
        File typedLuaFile = new File(typedLuaDir, "tlc")
        File plainLuaOutDir = new File(project.buildDir, "intermediates/splot-plain-lua")
        String userLuaPath = "${project.projectDir}/${project.splot.luaSourcesPath}"
        String splotTLPath = "${splotProject.projectDir}/${project.splot.luaSourcesPath}"
        String luaPath = "${userLuaPath}/?.lua;${userLuaPath}/?/init.lua;${splotTLPath}/?.lua;${splotTLPath}/?/init.lua;${typedLuaDir}/?.lua;${typedLuaDir}/typedlua/?.lua;;"

        if (!extractedArchivePath.exists()) {
            extractSelf(extractedArchivePath)
        }

        srcFiles.getFiles().each { File file ->
            String basePath = tlModPatt.matcher(file.absolutePath).replaceAll(/$1/)
            File outPath = new File(plainLuaOutDir, "${basePath}.lua")
            outPath.parentFile.mkdirs()

            File modulePath = new File(destDir, basePath)
            File bytecodeOutPath = new File("${modulePath}.lua")
            bytecodeOutPath.parentFile.mkdirs()

            def tlCompileCommand = [ "${luajitPath}", typedLuaFile.absolutePath, "-s", "-o", outPath.absolutePath, "${file.getAbsolutePath()}"]
            println tlCompileCommand
            Process tlCompileProcess = tlCompileCommand.execute(["LUA_PATH=${luaPath}"], null)
            tlCompileProcess.in.eachLine { line -> println line }
            tlCompileProcess.err.eachLine { line -> println line }

            def bcCompileCommand = [ "${luajitPath}", "-b", "-t", "raw", outPath.absolutePath, bytecodeOutPath.absolutePath]
            println bcCompileCommand
            Process bcCompileProcess = bcCompileCommand.execute()
            bcCompileProcess.in.eachLine { line -> println line }
            bcCompileProcess.err.eachLine { line -> println line }
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

class SplotPluginExtension {
    def String luajitPath = "/usr/local/bin/luajit"
    def String luaSourcesPath = "src/main/lua"
}