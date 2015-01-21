package io.splot.plugin;

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.security.CodeSource
import java.util.regex.Pattern

public class SplotPlugin implements Plugin<Project> {
    static final LUA_MAJOR_VERSION = 5
    static final LUA_MINOR_VERSION = 3
    static final luaVersionPatt = Pattern.compile("^Lua (\\d+)\\.(\\d+)\\.(\\d+).*")

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
            def javaBuildDir = new File(project.buildDir, "generated/source/splot-gen");
            def javaDestDir = new File(javaBuildDir, "splot");
            sourceSets {
                main.java.srcDirs project.splot.luaSourcesPath, javaBuildDir
            }

            def variants = plugin.class.name.endsWith('.LibraryPlugin') ? libraryVariants : applicationVariants

            variants.all {
                def variantName = name
                def luaDestDir = new File(project.buildDir, "intermediates/assets/${variantName}/splot_lua")
                def taskName = "splot${variantName.capitalize()}"

                project.task(taskName) {
                    def luaSources = project.splot.luaSourcesPath
                    def tlModPatt = Pattern.compile("^.*${luaSources}/(.*).tl")
                    def srcFiles = project.fileTree(luaSources).include('**/*.tl')

                    inputs.file srcFiles
                    outputs.dir luaDestDir
                    outputs.dir javaDestDir

                    doLast {
                        checkLuaVersion((String)project.splot.luacPath)
                        checkLuaVersion((String)project.splot.luaPath)
                        checkLuaDependencies((String)project.splot.luaPath)
                        compileLuaFiles(splotProject, project, luaDestDir, javaDestDir, (String)project.splot.luaPath, (String)project.splot.luacPath, srcFiles, tlModPatt)
                    }
                }
                preBuild.dependsOn(taskName)
            }
        }
    }

    static boolean checkLuaVersion(String luaPath) {
        Process proc = "${luaPath} -v".execute()
        proc.waitFor()
        String versionLine = proc.text
        def matcher = luaVersionPatt.matcher(versionLine)
        if (!matcher) {
            throw new GradleException("${luaPath} missing or version invalid. ${LUA_MAJOR_VERSION}.${LUA_MINOR_VERSION}.x is required.")
        }
        def majorVerStr = matcher.group(1)
        def minorVerStr = matcher.group(2)
        def patchVerStr = matcher.group(3)
        if (!majorVerStr.toInteger().equals(LUA_MAJOR_VERSION)) {
            throw new GradleException("${luaPath} version ${LUA_MAJOR_VERSION}.${LUA_MINOR_VERSION}.x is required (you have ${majorVerStr}.${minorVerStr}.${patchVerStr}).")
        }
        if (!minorVerStr.toInteger().equals(LUA_MINOR_VERSION)) {
            throw new GradleException("${luaPath} version ${LUA_MAJOR_VERSION}.${LUA_MINOR_VERSION}.x is required (you have ${majorVerStr}.${minorVerStr}.${patchVerStr}).")
        }
    }

    static boolean checkLuaDependencies(String luaPath) {
        Process requireProc = ["${luaPath}", "-e", "require 'lpeg'"].execute()
        requireProc.waitFor()
        if (requireProc.exitValue() != 0) {
            throw new GradleException("${luaPath} could not find the LPeg library. Make sure it's accessible from your Lua installation.")
        }
    }

    static void compileLuaFiles(Project splotProject, Project project, File luaDestDir, File javaDestDir, String luaPath, String luacPath, def srcFiles, Pattern tlModPatt) {
        File extractedArchivePath = new File(project.buildDir, "splot-plugin-jar")
        String typedLuaDir = new File(extractedArchivePath.toString(), "typedlua")
        String splotGenDir = new File(extractedArchivePath.toString(), "splot-code-gen")
        File splotCodeGenDistDir = new File(splotGenDir, "dist")
        File typedLuaFile = new File(typedLuaDir, "tlc")
        File plainLuaOutDir = new File(project.buildDir, "intermediates/splot-plain-lua")
        String userLuaPath = "${project.projectDir}/${project.splot.luaSourcesPath}"
        String splotTLPath = "${splotProject.projectDir}/${project.splot.luaSourcesPath}"
        String luaPackagesPath = "${userLuaPath}/?.lua;${userLuaPath}/?/init.lua;${splotTLPath}/?.lua;${splotTLPath}/?/init.lua;${typedLuaDir}/?.lua;${typedLuaDir}/typedlua/?.lua;;"
        String splotGenPackagesPath = "${splotGenDir}/dist/?.lua;${splotGenDir}/dist/?/init.lua;${luaPackagesPath}"
        File generatorFile = new File(splotGenDir, "dist/main.lua")

        if (!extractedArchivePath.exists()) {
            extractSelf(extractedArchivePath)
        }
        if (!splotCodeGenDistDir.exists()) {
            compileSplotCodeGen(luaPath, extractedArchivePath, splotCodeGenDistDir)
        }

        srcFiles.getFiles().each { File file ->
            String basePath = tlModPatt.matcher(file.absolutePath).replaceAll(/$1/)
            File plainLuaOutPath = new File(plainLuaOutDir, "${basePath}.lua")
            plainLuaOutPath.parentFile.mkdirs()
            compileToPlainLua(luaPath, luaPackagesPath, typedLuaFile, file, plainLuaOutPath)

            File modulePath = new File(luaDestDir, basePath)
            File byteCodeOutPath = new File("${modulePath}.lua")
            byteCodeOutPath.parentFile.mkdirs()
            compileToBytecode(luacPath, plainLuaOutPath, byteCodeOutPath)

            String moduleName = file.name.replaceAll("(.*)(\\.tl)\$", "\$1")
            if (moduleName.contentEquals(basePath)) {
                File javaOutFile = new File(javaDestDir, "${moduleName.capitalize()}.java")
                javaOutFile.parentFile.mkdirs()
                generateJavaCode(luaPath, splotGenPackagesPath, generatorFile, file, javaOutFile, moduleName)
            }
        }
    }

    static void compileToPlainLua(String luaPath, String luaPackagesPath, File tlcFile, File typedLuaFile, File outPath) {
        def tlCompileCommand = [ "${luaPath}", tlcFile.absolutePath, "-o", outPath.absolutePath, "${typedLuaFile.getAbsolutePath()}"]
        println tlCompileCommand
        Process tlCompileProcess = tlCompileCommand.execute(["LUA_PATH=${luaPackagesPath}"], null)
        tlCompileProcess.in.eachLine { line -> println line }
        tlCompileProcess.err.eachLine { line -> println line }
        tlCompileProcess.waitFor()
        if (tlCompileProcess.exitValue() != 0) {
            throw new GradleException("Could not compile Typed Lua file ${typedLuaFile}.")
        }
    }

    static void compileToBytecode(String luacPath, File plainLuaFile, File bytecodeOutPath) {
        // luac produces byte-code which is not portable between different platforms
//        def bcCompileCommand = [ "${luacPath}", "-o", bytecodeOutPath.absolutePath, plainLuaFile.absolutePath]
        def bcCompileCommand = [ "cp", plainLuaFile.absolutePath, bytecodeOutPath.absolutePath]
        println bcCompileCommand
        Process bcCompileProcess = bcCompileCommand.execute()
        bcCompileProcess.in.eachLine { line -> println line }
        bcCompileProcess.err.eachLine { line -> println line }
        bcCompileProcess.waitFor()
        if (bcCompileProcess.exitValue() != 0) {
            throw new GradleException("Could not compile Lua file ${plainLuaFile} to byte-code.")
        }
    }

    static void generateJavaCode(String luaPath, String luaPackagesPath, File generator, File typedLuaFile, File javaOutputFile, String moduleName) {
        def tlCompileCommand = [ "${luaPath}", generator.absolutePath, "${typedLuaFile.getAbsolutePath()}", moduleName, javaOutputFile.absolutePath]
        println tlCompileCommand
        Process tlCompileProcess = tlCompileCommand.execute(["LUA_PATH=${luaPackagesPath}"], null)
        tlCompileProcess.in.eachLine { line -> println line }
        tlCompileProcess.err.eachLine { line -> println line }
        tlCompileProcess.waitFor()
        if (tlCompileProcess.exitValue() != 0) {
            throw new GradleException("Could not generate Java file for ${typedLuaFile}.")
        }
    }

    static void extractSelf(File extractedArchivePath) {
        CodeSource src = SplotPlugin.class.getProtectionDomain().getCodeSource()
        AntBuilder ant = new AntBuilder()
        ant.unzip(src: src.getLocation().path, dest: extractedArchivePath.absolutePath, overwrite: "true") {
            patternset() {
                // The directories should be basically the same as in splot-gradle/build.gradle
                include(name: "typedlua/tlc")
                include(name: "typedlua/typedlua/**/*.tld")
                include(name: "typedlua/typedlua/**/*.lua")
                include(name: "splot-code-gen/src/*.lua")
                include(name: "splot-code-gen/src/*.tl")
                include(name: "splot-code-gen/compile.sh")
            }
        }
    }

    static void compileSplotCodeGen(String luaPath, File extractedArchivePath, File splotCodeGenDistDir) {
        Process tlCompileProcess = ["bash", "${extractedArchivePath}/splot-code-gen/compile.sh"].execute(["TLC=${luaPath} ${extractedArchivePath}/typedlua/tlc", "TLC_MODULES=${extractedArchivePath}/typedlua/?.lua;"], null)
        tlCompileProcess.in.eachLine { line -> println line }
        tlCompileProcess.err.eachLine { line -> println line }
        tlCompileProcess.waitFor()
        if (tlCompileProcess.exitValue() != 0) {
            throw new GradleException("Could not compile splot-code-gen.")
        }
    }
}

class SplotPluginExtension {
    def String luaPath = "/usr/local/bin/lua"
    def String luacPath = "/usr/local/bin/luac"
    def String luaSourcesPath = "src/main/lua"
}