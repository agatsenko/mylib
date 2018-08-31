addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.3")

// show dependencies:
// * dependencyTree: Shows an ASCII tree representation of the project's dependencies
// * dependencyBrowseGraph: Opens a browser window with a visualization of the dependency graph (courtesy of graphlib-dot + dagre-d3
// * dependencyList: Shows a flat list of all transitive dependencies on the sbt console (sorted by organization and name)
// * whatDependsOn <organization> <module> <revision>: Find out what depends on an artifact. Shows a reverse dependency tree for the selected module
// * ivyReport: let's ivy generate the resolution report for you project. Use show ivyReport for the filename of the generated report
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.2")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.18")
