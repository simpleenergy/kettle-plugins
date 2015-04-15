name := "IronIO"

organizationName := "com.simpleenergy"

scalaVersion := "2.11.5"

val kettleVersion = "4.4.1-GA"

resolvers ++= Seq(
  "pentaho-releases" at "http://repository.pentaho.org/artifactory/repo/",
  "swt-repo" at "https://swt-repo.googlecode.com/svn/repo/"
)

libraryDependencies ++= Seq(
  "io.iron.ironmq"       % "ironmq"                             % "0.0.19",
  "com.google.code.gson" % "gson"                               % "2.3.1",
  "org.apache.commons"   % "commons-lang3"                      % "3.3.2"       % "provided",
  "commons-httpclient"   % "commons-httpclient"                 % "3.1"         % "provided",
  "commons-vfs"          % "commons-vfs"                        % "1.0"         % "provided",
  "pentaho-kettle"       % "kettle-engine"                      % kettleVersion % "provided",
  "pentaho-kettle"       % "kettle-core"                        % kettleVersion % "provided",
  "pentaho-kettle"       % "kettle-db"                          % kettleVersion % "provided",
  "pentaho-kettle"       % "kettle-ui-swt"                      % kettleVersion % "provided",
  "org.eclipse.swt"      % "org.eclipse.swt.win32.win32.x86_64" % "4.3"         % "provided"
)
