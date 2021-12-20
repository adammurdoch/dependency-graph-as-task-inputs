import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.component.ComponentArtifactIdentifier;
import org.gradle.api.artifacts.result.ResolvedVariantResult;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public abstract class ReportArtifactMetadataTask extends DefaultTask {
    @Input
    public abstract ListProperty<ComponentArtifactIdentifier> getArtifactIds();

    @Input
    public abstract ListProperty<ResolvedVariantResult> getArtifactVariants();

    @InputFiles
    public abstract ListProperty<RegularFile> getArtifactFiles();

    @OutputFile
    public abstract RegularFileProperty getOutputFile();

    @TaskAction
    public void report() throws IOException {
        File outputFile = getOutputFile().getAsFile().get();
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            List<ComponentArtifactIdentifier> ids = getArtifactIds().get();
            List<ResolvedVariantResult> variants = getArtifactVariants().get();
            List<RegularFile> files = getArtifactFiles().get();
            for (int i = 0; i < ids.size(); i++) {
                ComponentArtifactIdentifier id = ids.get(i);
                ResolvedVariantResult variant = variants.get(i);
                RegularFile file = files.get(i);
                writer.print("FILE ");
                writer.println(file.getAsFile().getName());
                writer.print("  id: ");
                writer.println(id.getDisplayName());
                writer.print("  variant: ");
                writer.println(variant.getDisplayName());
                writer.print("  size: ");
                writer.println(file.getAsFile().length());
                writer.println();
            }
        }
        System.out.println("Wrote report to " + outputFile);
    }
}
