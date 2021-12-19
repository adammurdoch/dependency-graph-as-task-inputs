import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.result.DependencyResult;
import org.gradle.api.artifacts.result.ResolvedComponentResult;
import org.gradle.api.artifacts.result.ResolvedDependencyResult;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public abstract class ReportDependencyGraphTask extends DefaultTask {
    @Input
    public abstract Property<ResolvedComponentResult> getRootComponent();

    @OutputFile
    public abstract RegularFileProperty getOutputFile();

    @TaskAction
    public void report() throws IOException {
        File outputFile = getOutputFile().getAsFile().get();
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            Set<ResolvedComponentResult> seen = new HashSet<>();
            reportComponent(getRootComponent().get(), writer, seen, "");
        }
        System.out.println("Wrote report to " + outputFile);
    }

    private void reportComponent(
            ResolvedComponentResult component,
            PrintWriter writer,
            Set<ResolvedComponentResult> seen,
            String indent
    ) {
        writer.print(component.getId().getDisplayName());
        if (seen.add(component)) {
            writer.println();
            String newIndent = indent + "  ";
            for (DependencyResult dependency : component.getDependencies()) {
                writer.print(newIndent);
                writer.print(dependency.getRequested().getDisplayName());
                writer.print(" -> ");
                if (dependency instanceof ResolvedDependencyResult) {
                    ResolvedDependencyResult resolvedDependency = (ResolvedDependencyResult) dependency;
                    reportComponent(resolvedDependency.getSelected(), writer, seen, newIndent);
                } else {
                    writer.println(" -> not found");
                }
            }
        } else {
            writer.println(" (already seen)");
        }
    }
}
