package com.anosym.vjax.plugin;

import com.anosym.vjax.v3.wrapper.VObjectWrapper;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 *
 * @author marembo
 */
@Mojo(name = "vjax", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class VObjectWrapperPlugin extends AbstractMojo {

    /**
     * Fully qualified name of the source directory for the generated sources. There is no default.
     * The final name will have generated-sources/vjax-wrapper appended based on maven source dir structure and the value
     * of {@link #sourceDirType}
     */
    @Parameter(required = true)
    private File sourceDir;
    @Parameter(property = "project.runtimeClasspathElements", required = true, readonly = true)
    private List<String> classpath;
    /**
     * Options include main, test, generated-sources or any other user specified directory.
     */
    @Parameter(defaultValue = "generated-sources")
    private String sourceDirType;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (sourceDir == null) {
            throw new MojoFailureException("Source Dir is required");
        }
        try {
            init();
            File path = new File(sourceDir, sourceDirType + "/vjax-wrapper");
            System.out.println("Generate sources directory...." + path.getAbsolutePath());
            VObjectWrapper vow = new VObjectWrapper(path.getAbsolutePath());
            vow.process();
        } catch (Exception ex) {
            throw new MojoExecutionException("Error generating sources", ex);
        }
    }

    private void init() {
        try {
            Set<URL> urls = new HashSet<URL>();
            for (String element : classpath) {
                urls.add(new File(element).toURI().toURL());
            }
            ClassLoader contextClassLoader = URLClassLoader.newInstance(
                    urls.toArray(new URL[0]),
                    Thread.currentThread().getContextClassLoader());
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
