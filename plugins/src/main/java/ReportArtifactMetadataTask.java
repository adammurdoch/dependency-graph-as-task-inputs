import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.component.ComponentArtifactIdentifier;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class ReportArtifactMetadataTask extends DefaultTask {
    @Input
    public abstract ListProperty<ComponentArtifactIdentifier> getArtifacts();

    @OutputFile
    public abstract RegularFileProperty getOutputFile();

    @TaskAction
    public void report() throws IOException {
        File outputFile = getOutputFile().getAsFile().get();
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            for (ComponentArtifactIdentifier id : getArtifacts().get()) {
                writer.println(id.getDisplayName());
            }
        }
        System.out.println("Wrote report to " + outputFile);
    }
}
