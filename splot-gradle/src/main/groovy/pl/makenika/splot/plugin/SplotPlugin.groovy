package pl.makenika.splot.plugin;

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.security.CodeSource
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

public class SplotPlugin implements Plugin<Project> {
    void apply(Project project) {
        def plugin = project.plugins.findPlugin('android')?:project.plugins.findPlugin('android-library')
        if (!plugin) {
            throw new GradleException('You must apply the Android plugin or the Android library plugin before using the SplotInternalPlugin plugin.')
        }

        project.android {
            sourceSets {
                main.java.srcDir('src/main/lua')
            }

            def luaSourcesPath = "src/main/lua"
            def tlExtPatt = Pattern.compile("(.*)(\\.tl)\$")
            def variants = plugin.class.name.endsWith('.LibraryPlugin') ? libraryVariants : applicationVariants
            variants.all {
                def variantName = name
                def taskName = "splot${variantName.capitalize()}"
                project.task(taskName) {
                    ext.srcFiles = project.fileTree(luaSourcesPath).include('**/*.tl')
                    ext.destDir = new File(project.buildDir, "generated/res/generated/${variantName}/raw")

                    doLast {
                        Path tmpDir = Files.createTempDirectory("splot")
                        URL typedLuaDirUrl = getClass().getClassLoader().getResource("typedlua")
                        println("typedLuaDirUrl: ${typedLuaDirUrl}")
                        println("typedLuaDirUrl URI: ${typedLuaDirUrl.toURI()}")
                        println("typedLuaDirUrl path: ${typedLuaDirUrl.path}")
//                        FileSystem fileSystem = FileSystems.newFileSystem(Paths.get(typedLuaDirUrl.path), getClass().getClassLoader())
                        // TODO extract typedlua dir from the jar into the tmpDir

                        Files.walkFileTree(Paths.get(typedLuaDirUrl.path), new SimpleFileVisitor<Path>() {
                            @Override
                            FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                println("visiting file: ${file}")
                                return FileVisitResult.CONTINUE;
                            }
                        })

                        destDir.mkdirs()
                        srcFiles.getFiles().each { File file ->
                            def outFileName = tlExtPatt.matcher(file.getName()).replaceAll(/$1.lua/)
//                            URL typedLuaDirUrl = getClass().getClassLoader().getResource("typedlua")
//                            File typedLuaDir = new File(typedLuaDirUrl.path)
                            File typedLuaFile = new File(typedLuaDir, "tlc")
                            println("TypedLua path: ${typedLuaDir.absolutePath}")
                            def compileCommand = [ "/usr/local/bin/luajit", typedLuaFile.absolutePath, "-s", "-o", "${destDir}/${outFileName}", "${file.getAbsolutePath()}"]
                            println compileCommand
                            Process compileProcess = compileCommand.execute(["LUA_PATH=${typedLuaDir}/?.lua;${typedLuaDir}/typedlua/?.lua;;"], null)
                            compileProcess.in.eachLine { line -> println line }
                            compileProcess.err.eachLine { line -> println line }
                        }
                    }
                }
                javaCompile.dependsOn(taskName)
            }
        }
    }

    File extractTypedLuaFiles() {
        Path tmpDir = Files.createTempDirectory("splot-tl")
        CodeSource src = MyClass.class.getProtectionDomain().getCodeSource()

        if (src != null) {
            URL jar = src.getLocation()
            ZipInputStream zip = new ZipInputStream(jar.openStream())
            while(true) {
                ZipEntry e = zip.getNextEntry()
                if (e == null) {
                    break
                }
                String name = e.getName()
                if (name.startsWith("typedlua/")) {
                    // TODO
                    // Files.copy...
                    
                }
            }
        } else {
            return null
        }
    }
}