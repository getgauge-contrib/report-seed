# Report Seed Project

A sample project that listens to [gauge](https://github.com/getgauge/gauge)'s execution and receives execution summary.

This report sample may be forked/changed/enhanced to suit custom reporting needs.

## Prerequisites

- JDK 1.8.

## Building

- clone/fork this repo
- delete `examples`, you do not need them to be shipped
- Change the template values to be specific to your project
  - `plugin.json` - add metadata relevant to your plugin
  - Choose a class name that is relevant to your plugin. It is a good idea to remove the `ExampleReporter.java`
  - `build.gradle` - change dependencies, metadata relevant to your plugin
    - Ensure that your jar manifest in has the correct `Main-class` attribute.
  - `launch.cmd`, `launch.sh` - change the command to invoke your jar.
  - clean/refactor the code as you please :)

- Ensure that your 
- `./gradlew build` - creates the binaries (using gradle's default build, no magic here)
- `./gradlew distro` - creates a zipped distributable gauge plugin
- `gauge install report-seed -f artifacts/report-seed-0.0.1.zip` - use gauge's file install to install the plugin.

## Usage

- `gauge init <language you like>`
- Edit `manifest.json`, add `report-seed` to `plugins`. End result would be something like this:

```
{
  "Language": "java",
  "Plugins": [
    "html-report",
    "report-seed"
  ]
}
```

- `gauge run specs` should invoke the plugin.

## Examples

Check out the `examples` folder.

# License

MIT. 