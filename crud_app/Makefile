all: setup
clean:
	rm -rf .idea
	rm -rf .idea_modules
	rm -rf target
runPlayIdea:
	play idea
removeBuildModule:
	rm -rf .idea_modules/*realtimepermit-build.iml
	sed -i 's/.*realtimepermit-build.iml.*//g' .idea/modules.xml
updateScalaCompilerName:
	sed -i 's/Scala 2.10.0/scala-2.10.0/g' .idea/scala_compiler.xml
	sed -i 's/Scala 2.10.0/scala-2.10.0/g' .idea/libraries/scala_2_10_0.xml
configureGitRoot:
	xmlstarlet edit --inplace --update "//project/component/mapping[@directory='']/@directory" --value "\$$PROJECT_DIR\$$/.." .idea/vcs.xml
	xmlstarlet edit --inplace --update "//project/component/mapping[@vcs='']/@vcs" --value "Git" .idea/vcs.xml
setup: clean runPlayIdea removeBuildModule updateScalaCompilerName configureGitRoot
