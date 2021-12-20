# Dependency graph results as task inputs

This repository contains a Gradle build that demonstrates how to write tasks that use dependency resolution results as
inputs using new APIs that are configuration cache compatible.

There are two different kinds of results that can be used by tasks:

- The dependency graph, represented via `ResolvedComponentResult`, which provides access to the components and dependencies between them arranged in a graph.
- Artifact metadata, represented via `ResolvedArtifactResult`, which provides access to an artifact in the result and some details about it.

The `plugins/` directory contains a plugin implementation that provides two tasks:

- `artifact-report` that generates a report on the artifacts in the project's runtime classpath.
- `graph-report` that generates a report on the dependency graph of the runtime classpath.

The artifact report task (`ReportArtifactMetadataTask`) declares both the artifact metadata and the artifact files as inputs,
but a task might instead use only the artifact metadata. The plugin uses the [ArtifactCollection.getResolvedArtifacts()](https://docs.gradle.org/nightly/javadoc/org/gradle/api/artifacts/ArtifactCollection.html#getResolvedArtifacts--) method to get a lazy `Provider` of the runtime classpath artifact meta-data to connect to the report task.

The graph report task (`ReportDependencyGraphTask`) declares the graph as an input. It does not declare the artifacts
as inputs. The plugin uses the [ResolutionResult.getRootComponent()](https://docs.gradle.org/nightly/javadoc/org/gradle/api/artifacts/result/ResolutionResult.html#getRootComponent--)
method to get a lazy `Provider` of the runtime classpath dependency graph to connect to the report task. 

This build has configuration caching enabled.

## Artifact metadata

To try the artifact report, run:

`> ./gradlew artifact-report --console verbose`

This builds the artifacts, then generates the report. It also writes a configuration cache entry.

Some things to try:

- Run this again. The configuration cache entry is reused and the report is up-to-date.
- Change a library source file, for example in `libs/lib2/`. The configuration cache entry is reused, the artifacts are rebuild and the report generated again. The artifact files are declared as inputs to the report task so when their content changes the report is run again. 
- Change the dependency graph, for example by changing `libs/lib2/build.gradle.kts`. In this case, the configuration cache entry is discarded because one of its inputs have changed, and the report is generated again.

## Dependency graph

To try the dependency graph, run:

`> ./gradlew graph-report --console verbose`

This generates the report and writes a configuration cache entry. Notice in this case the artifacts are not
built, as they are not available via `ResolvedComponentResult` and so do not form an input to the report task.

Some things to try:

- Run this again. The configuration cache entry is reused and the report is up-to-date.
- Change a library source file, for example in `libs/lib2/`. The report is up-to-date because the artifacts are not an input to the report task. 
- Change the dependency graph, for example by changing `libs/lib2/build.gradle.kts`. In this case, the configuration cache entry is discarded because one of its inputs have changed, and the report is generated again.
