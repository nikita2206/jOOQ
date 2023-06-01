/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Other licenses:
 * -----------------------------------------------------------------------------
 * Commercial licenses for this work are available. These replace the above
 * ASL 2.0 and offer limited warranties, support, maintenance, and commercial
 * database integrations.
 *
 * For more information, please visit: https://www.jooq.org/legal/licensing
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.jooq.codegen.maven;

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;
import static org.apache.maven.plugins.annotations.ResolutionScope.TEST;
import static org.jooq.Constants.XSD_CODEGEN;
import static org.jooq.codegen.GenerationTool.DEFAULT_TARGET_DIRECTORY;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.Adler32;

import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Target;
import org.jooq.util.jaxb.tools.MiniJAXB;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * The jOOQ Codegen Plugin
 *
 * @author Sander Plas
 * @author Lukas Eder
 */
@Mojo(
    name = "generate",
    defaultPhase = GENERATE_SOURCES,
    requiresDependencyResolution = TEST,
    threadSafe = true
)
public class Plugin extends AbstractMojo {

    /**
     * The Maven project.
     */
    @Parameter(
        property = "project",
        required = true,
        readonly = true
    )
    private MavenProject                 project;

    /**
     * An external configuration file that is appended to anything from the
     * Maven configuration, using Maven's <code>combine.children="append"</code>
     * semantics.
     */
    @Parameter(
        property = "jooq.codegen.configurationFile"
    )
    private String                       configurationFile;

    /**
     * An external set of configuration files that is appended to anything from
     * the Maven configuration, using Maven's
     * <code>combine.children="append"</code> semantics.
     */
    @Parameter(
        property = "jooq.codegen.configurationFiles"
    )
    private List<String>                 configurationFiles;

    /**
     * The base directory that should be used instead of the JVM's working
     * directory, to resolve all relative paths.
     */
    @Parameter(
        property = "jooq.codegen.basedir"
    )
    private String                       basedir;

    /**
     * Whether to skip the execution of the Maven Plugin for this module.
     */
    @Parameter(
        property = "jooq.codegen.skip"
    )
    private boolean                      skip;

    /**
     * The logging threshold.
     */
    @Parameter(
        property = "jooq.codegen.logging"
    )
    private org.jooq.meta.jaxb.Logging   logging;

    /**
     * The on-error behavior.
     */
    @Parameter(
        property = "jooq.codegen.onError"
    )
    private org.jooq.meta.jaxb.OnError   onError;

    /**
     * The on-unused behavior.
     */
    @Parameter(
        property = "jooq.codegen.onUnused"
    )
    private org.jooq.meta.jaxb.OnError   onUnused;

    /**
     * The jdbc settings.
     */
    @Parameter
    private org.jooq.meta.jaxb.Jdbc      jdbc;

    /**
     * The generator settings
     */
    @Parameter
    private org.jooq.meta.jaxb.Generator generator;

    /**
     * Files in the directories specified here should exhaustively
     * define all schemas, Jooq Codegen Maven plugin will use these
     * files to compute the checksum.
     * When Jooq classes are generated, Jooq will also add a file
     * with the checksum of these schema definitions, this way when
     * recompiling the codebase again you will not have to get up
     * the testcontainer, apply all migrations and regenerate Jooq classes.
     * Unless one of the SQL files is changed or a new one is added.
     */
    @Parameter(
            property = "jooq.codegen.schemaDefinitionInputs"
    )
    private List<String> schemaDefinitionInputs;

    /**
     * Specify this to force generation of Jooq classes even if the
     * checksum hasn't changed.
     */
    @Parameter(
            property = "jooq.codegen.forceGenerate"
    )
    private boolean forceGenerate;

    @Override
    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("Skipping jOOQ code generation");
            return;
        }

        if (configurationFiles != null && !configurationFiles.isEmpty())
            for (String file : configurationFiles)
                read(file);
        else if (configurationFile != null)
            read(configurationFile);

        // [#5286] There are a variety of reasons why the generator isn't set up
        //         correctly at this point. We'll log them all here.
        if (generator == null) {
            getLog().error("Incorrect configuration of jOOQ code generation tool");
            getLog().error(
                  "\n"
                + "The jOOQ-codegen-maven module's generator configuration is not set up correctly.\n"
                + "This can have a variety of reasons, among which:\n"
                + "- Your pom.xml's <configuration> contains invalid XML according to " + XSD_CODEGEN + "\n"
                + "- There is a version or artifact mismatch between your pom.xml and your commandline");

            throw new MojoExecutionException("Incorrect configuration of jOOQ code generation tool. See error above for details.");
        }

        ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
        URLClassLoader pluginClassLoader = getClassLoader();

        try {

            // [#2886] Add the surrounding project's dependencies to the current classloader
            Thread.currentThread().setContextClassLoader(pluginClassLoader);

            // [#9727] The Maven basedir may be overridden by explicit configuration
            String actualBasedir = basedir == null ? project.getBasedir().getAbsolutePath() : basedir;

            // [#5881] Target is allowed to be null
            if (generator.getTarget() == null)
                generator.setTarget(new Target());

            if (generator.getTarget().getDirectory() == null)
                generator.getTarget().setDirectory(DEFAULT_TARGET_DIRECTORY);

            Configuration configuration = new Configuration();
            configuration.setLogging(logging);
            configuration.setOnError(onError);
            configuration.setOnUnused(onUnused);
            configuration.setJdbc(jdbc);
            configuration.setGenerator(generator);
            configuration.setBasedir(actualBasedir);

            if (getLog().isDebugEnabled())
                getLog().debug("Using this configuration:\n" + configuration);

            int configurationHashCode = configuration.toString().hashCode();
            String targetDirectory = generator.getTarget().getDirectory();

            if (shouldRegenerateAccordingToInputChecksum(configurationHashCode, targetDirectory)) {
                Set<String> outFiles = GenerationTool.generate(configuration);
                saveChecksumFile(configurationHashCode, targetDirectory, outFiles);
            } else {
                getLog().info("Skipping class generation, because file digests match");
            }
        }
        catch (Exception ex) {
            throw new MojoExecutionException("Error running jOOQ code generation tool", ex);
        }
        finally {

            // [#2886] Restore old class loader
            Thread.currentThread().setContextClassLoader(oldCL);

            // [#7630] Close URLClassLoader to help free resources
            try {
                pluginClassLoader.close();
            }

            // Catch all possible errors to avoid suppressing the original exception
            catch (Throwable e) {
                getLog().error("Couldn't close the classloader.", e);
            }
        }

        project.addCompileSourceRoot(generator.getTarget().getDirectory());
    }

    private void read(String file) {
        getLog().info("Reading external configuration: " + file);
        File f = new File(file);

        if (!f.isAbsolute())
            f = new File(project.getBasedir(), file);

        try (FileInputStream in = new FileInputStream(f)) {
            Configuration configuration = GenerationTool.load(in);
            logging = MiniJAXB.append(logging, configuration.getLogging());
            onError = MiniJAXB.append(onError, configuration.getOnError());
            onUnused = MiniJAXB.append(onUnused, configuration.getOnUnused());
            jdbc = MiniJAXB.append(jdbc, configuration.getJdbc());
            generator = MiniJAXB.append(generator, configuration.getGenerator());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private URLClassLoader getClassLoader() throws MojoExecutionException {
        try {
            List<String> classpathElements = project.getRuntimeClasspathElements();
            URL urls[] = new URL[classpathElements.size()];

            for (int i = 0; i < urls.length; i++)
                urls[i] = new File(classpathElements.get(i)).toURI().toURL();

            return new URLClassLoader(urls, getClass().getClassLoader());
        }
        catch (Exception e) {
            throw new MojoExecutionException("Couldn't create a classloader.", e);
        }
    }

    /**
     * Validate all files in the schemaDefinitionInputs directories, by constructing a checksum
     * of these files and verifying that the target directory already contains that checksum.
     * If the target directory doesn't contain the checksum, or checksums don't match, then
     * we need to regenerate Jooq classes.
     */
    private boolean shouldRegenerateAccordingToInputChecksum(int configurationHashCode, String targetDirectory) {
        if (forceGenerate || schemaDefinitionInputs == null || schemaDefinitionInputs.isEmpty())
            return true;

        // Checksum file contains:
        // 1. first line is a checksum hash, which is calculated based on the current Jooq
        //     configuration and on the checksum of all input files
        // 2. subsequent lines are filenames

        File checksumFile = Paths.get(project.getBasedir().getPath(), targetDirectory, ".jooq-checksum").toFile();
        if (!checksumFile.exists() || !checksumFile.isFile()) {
            getLog().info("Checksum file doesn't exist, will regenerate Jooq classes.");
            return true;
        }

        String savedChecksum;
        Set<String> generatedFiles = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(checksumFile))) {
            String checksumLine = reader.readLine();
            if (checksumLine == null) {
                getLog().info("The checksum file is empty, will regenerate Jooq classes.");
                return true;
            }

            savedChecksum = checksumLine.trim();
            reader.lines().filter(s -> !s.isEmpty()).forEach(generatedFiles::add);
        } catch (IOException e) {
            getLog().warn("Couldn't read checksum file. If you're unsure " +
                    "how to solve this issue, try removing the .jooq-checksum file", e);
            return true;
        }

        String currentInputFilesChecksum;
        try {
            currentInputFilesChecksum = calculateInputSchemaDefinitionsChecksum(configurationHashCode);
        } catch (MojoExecutionException e) {
            getLog().warn("Couldn't calculate the current checksum.", e);
            return true;
        }

        if (!savedChecksum.equals(currentInputFilesChecksum)) {
            getLog().info("Checksums didn't match, will regenerate Jooq classes.");
            return true;
        }

        // once we see that checksums match, we just want to ensure that the files are still actually there

        for (String generatedFile : generatedFiles) {
            if (Files.notExists(Paths.get(generatedFile))) {
                getLog().info("Detected that a generated file " + generatedFile
                        + " doesn't exist anymore, will regenerate Jooq classes.");
                return true;
            }
        }

        getLog().info("Checksums matched, all generated files exist, will skip " +
                "regeneration of Jooq classes.");

        return false;
    }

    private void saveChecksumFile(int configurationHashCode, String targetDirectory, Set<String> generatedFiles) {
        if (schemaDefinitionInputs == null || schemaDefinitionInputs.isEmpty())
            return;

        String inputDefinitionsChecksum;
        try {
            inputDefinitionsChecksum = calculateInputSchemaDefinitionsChecksum(configurationHashCode);
        } catch (MojoExecutionException e) {
            getLog().warn("Couldn't calculate the current checksum.", e);
            return;
        }

        byte[] lineSeparator = System.lineSeparator().getBytes();
        try (FileOutputStream os = new FileOutputStream(Paths.get(project.getBasedir().getPath(), targetDirectory, ".jooq-checksum").toFile(), false)) {
            os.write(inputDefinitionsChecksum.getBytes());
            os.write(lineSeparator);

            for (String generatedFile : generatedFiles) {
                os.write(generatedFile.getBytes());
                os.write(lineSeparator);
            }
        } catch (IOException e) {
            getLog().warn("Couldn't write to the .jooq-checksum file", e);
        }
    }

    private String calculateInputSchemaDefinitionsChecksum(int configurationHashCode)
            throws MojoExecutionException {
        Adler32 checksum = new Adler32();
        checksum.update(configurationHashCode);
        for (String schemaDefinitionsInputDirectory : schemaDefinitionInputs) {
            List<String> schemaDefinitionFiles;
            try {
                schemaDefinitionFiles = Files.walk(Paths.get(schemaDefinitionsInputDirectory))
                        .filter(p -> p.toFile().isFile())
                        .map(Path::toString)
                        .sorted()
                        .toList();
            } catch (IOException e) {
                throw new MojoExecutionException("Couldn't list files under the directory "
                        + schemaDefinitionsInputDirectory, e);
            }

            for (String schemaFile : schemaDefinitionFiles) {
                checksum.update(schemaFile.getBytes());

                try {
                    checksum.update(Files.readAllBytes(Paths.get(schemaFile)));
                } catch (IOException e) {
                    throw new MojoExecutionException("Couldn't read " + schemaFile
                            + " while calculating the checksum.", e);
                }
            }
        }

        return Long.toString(checksum.getValue());
    }
}
