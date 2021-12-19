import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.result.ArtifactResult;
import org.gradle.api.artifacts.result.ResolvedArtifactResult;
import org.gradle.api.provider.Provider;

import java.util.Set;
import java.util.stream.Collectors;

public class TestPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        target.getPlugins().apply("java-library");

        target.getTasks().register("artifacts-report", ReportArtifactMetadataTask.class, t -> {
            Provider<Set<ResolvedArtifactResult>> artifacts = target.getConfigurations().getByName("runtimeClasspath").getIncoming().getArtifacts().getResolvedArtifacts();
            t.getArtifacts().set(artifacts.map(s -> s.stream().map(ArtifactResult::getId).collect(Collectors.toList())));
            t.getOutputFile().set(target.getLayout().getBuildDirectory().file("artifacts.txt"));
        });
    }
}
