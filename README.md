SimpleEnergy Kettle Plugins
===========================

This repo holds a collection of Kettle plugins we've developed for various functionality not found in the base Kettle
distribution. They're not particularly fancy, but we use them in production and they're reasonably stable. Please file an
issue if you run into any problems.

# Build Instructions #

To build these plugins you'll need the Scala Build Tool (http://www.scala-sbt.org/). Once you have that installed, you can
run `sbt assembly` in the plugin subdirectory and then copy the `plugin.xml`, `icon.png`, and `target/scala_2.11/*.jar` to a
new subdirectory under the `plugins/steps/` directory of your Kettle installation.


