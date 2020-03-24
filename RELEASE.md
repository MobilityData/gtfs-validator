# Release instructions

To create a release jar file, we use the IntelliJ artifact build feature

- Go to File->Project Structure
- Select Artifacts in the left pane
- Add a new one by clicking the '+' button
  - of type JAR->From modules with dependencies
- Select the Main class from cli-app module
- Place the META-INF file at the project's root
- In the output Layout, add both cli-app and in-memory-simple modules main resource directory content

- Build the jar through Build->Build Artifacts

