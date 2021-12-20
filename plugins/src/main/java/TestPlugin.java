import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Transformer;
import org.gradle.api.artifacts.component.ComponentArtifactIdentifier;
import org.gradle.api.artifacts.result.ResolvedArtifactResult;
import org.gradle.api.artifacts.result.ResolvedComponentResult;
import org.gradle.api.artifacts.result.ResolvedVariantResult;
import org.gradle.api.file.Directory;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Provider;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        target.getPlugins().apply("java-library");

        target.getTasks().register("artifacts-report", ReportArtifactMetadataTask.class, t -> {
            Provider<Set<ResolvedArtifactResult>> artifacts = target.getConfigurations().getByName("runtimeClasspath").getIncoming().getArtifacts().getResolvedArtifacts();
            t.getArtifactIds().set(artifacts.map(new IdExtractor()));
            t.getArtifactVariants().set(artifacts.map(new VariantExtractor()));
            t.getArtifactFiles().set(artifacts.map(new FileExtractor(target.getLayout())));
            t.getOutputFile().set(target.getLayout().getBuildDirectory().file("artifacts.txt"));
        });

        target.getTasks().register("graph-report", ReportDependencyGraphTask.class, t -> {
            Provider<ResolvedComponentResult> rootComponent = target.getConfigurations().getByName("runtimeClasspath").getIncoming().getResolutionResult().getRootComponent();
            t.getRootComponent().set(rootComponent);
            t.getOutputFile().set(target.getLayout().getBuildDirectory().file("graph.txt"));
        });
    }

    static class IdExtractor implements Transformer<List<ComponentArtifactIdentifier>, Collection<ResolvedArtifactResult>> {
        @Override
        public List<ComponentArtifactIdentifier> transform(Collection<ResolvedArtifactResult> artifacts) {
            return artifacts.stream().map(ResolvedArtifactResult::getId).collect(Collectors.toList());
        }
    }

    static class VariantExtractor implements Transformer<List<ResolvedVariantResult>, Collection<ResolvedArtifactResult>> {
        @Override
        public List<ResolvedVariantResult> transform(Collection<ResolvedArtifactResult> artifacts) {
            return artifacts.stream().map(ResolvedArtifactResult::getVariant).collect(Collectors.toList());
        }
    }

    static class FileExtractor implements Transformer<List<RegularFile>, Collection<ResolvedArtifactResult>> {
        private final ProjectLayout projectLayout;

        public FileExtractor(ProjectLayout projectLayout) {
            this.projectLayout = projectLayout;
        }

        @Override
        public List<RegularFile> transform(Collection<ResolvedArtifactResult> artifacts) {
            Directory projectDirectory = projectLayout.getProjectDirectory();
            return artifacts.stream().map(a -> projectDirectory.file(a.getFile().getAbsolutePath())).collect(Collectors.toList());
        }
    }
}
