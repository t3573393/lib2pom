# lib2pom

this tools used for convert the traditional lib jars to the pom file content. Try to resolve the jar info and dependency.

This use the REST api and other lib.

# usage

this lib include two ways:

## one:
use this by a executable jar in command line model:

commands usage->

	  java -jar lib2pom.jar [-if ${inflate out test lib}] [-p] [-o ${output pom file target dir}] [${source lib dir}]
	
	  -p :print the merget result to the log or console
	  -if: inflate the pom result to jar lists
	  -o: the output pom result dir
	  source lib dir: the source lib to resolve

besides you can use a file named tools.properties to set the params:

		inputLibPath: the lib input path
		outPutPomFileName: output pom file absolute path
		inflateOutPath: the inflate target path

		needInflate: switch for inflate [true/false]
		needPrintoutResult: switch for printout merge result [true/false]

		jarInfoSource: jar information source [archiva]

		archivaBaseUrl: archiva base url
		archivaRestServicesPath: archiva services path [restServices]
		archivaUser: archiva rest api user [guest]
		archivaPassword: archiva rest api user passowrd []

    
## two
use the maven plugin to do, but the plugin you should put to your private respository. It is not in the center repo.

example:

		<plugin>
          <groupId>org.fartpig</groupId>
          <artifactId>lib2pom-maven-plugin</artifactId>
          <version>0.3.0-RELEASE</version>
          <executions>
            <execution>
              <id>test-lib2pom</id>
              <goals>
                <goal>
                  resolve
                </goal>
              </goals>
            </execution>
          </executions>
          <configuration>
            <inputLibPath>***</inputLibPath>
          </configuration>
		</plugin>
