name := "IronIO"

organizationName := "com.simpleenergy"

scalaVersion := "2.11.5"

resolvers ++= Seq(
  "pentaho-releases" at "http://repository.pentaho.org/artifactory/repo/",
  "swt-repo" at "https://swt-repo.googlecode.com/svn/repo/"
)

libraryDependencies ++= Seq(
  "io.iron.ironmq"       % "ironmq" % "0.0.19",
  "com.google.code.gson" % "gson"   % "2.3.1",
  "org.apache.commons"   % "commons-lang3" % "3.3.2"
)

// <sigh>. SWT development is horrendous
unmanagedJars in Compile ++= {
    val base = baseDirectory.value / ".." / ".."
    val baseDirectories = (base / "lib") +++ (base / "libswt" / "osx64")
    val customJars = (baseDirectories ** "*.jar")
    customJars.classpath
}

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = true, includeDependency = false)
