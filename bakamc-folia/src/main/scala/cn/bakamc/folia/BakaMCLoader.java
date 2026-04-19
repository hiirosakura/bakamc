package cn.bakamc.folia;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

@SuppressWarnings("UnstableApiUsage")
public class BakaMCLoader implements PluginLoader {
    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        var resolver = new MavenLibraryResolver();
        resolver.addRepository(new RemoteRepository.Builder("central", "default", MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR).build());

        //scala
        resolver.addDependency(new Dependency(new DefaultArtifact("org.scala-lang:scala3-library_3:3.7.4"), null));

        //database - slick
        resolver.addDependency(new Dependency(new DefaultArtifact("com.typesafe.slick:slick_3:3.6.1"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("com.typesafe.slick:slick-hikaricp_3:3.6.1"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("com.zaxxer:HikariCP:7.0.2"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("com.mysql:mysql-connector-j:9.6.0"), null));

        classpathBuilder.addLibrary(resolver);
    }
}
