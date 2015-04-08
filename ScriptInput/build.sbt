name := "ScriptInput"

organizationName := "com.simpleenergy"

scalaVersion := "2.11.5"

resolvers ++= Seq(
  "pentaho-releases" at "http://repository.pentaho.org/artifactory/repo/",
  "swt-repo" at "https://swt-repo.googlecode.com/svn/repo/"
)

// <sigh>. SWT development is horrendous
unmanagedJars in Compile ++= {
    val base = baseDirectory.value / ".." / ".."
    val baseDirectories = (base / "lib") +++ (base / "libswt" / "osx64")
    val customJars = (baseDirectories ** "*.jar")
    customJars.classpath
}

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = true, includeDependency = false)
