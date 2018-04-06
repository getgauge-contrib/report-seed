# Example - csv report with gauge

> DISCLAIMER: This is just an example. Not meant to be used as is in a project. 

This project is an example implementation of the [report-seed](https://github.com/getgauge-contrib/report-seed)

The idea is to get the raw execution result and transform it to csv.

> Note: There are a good number of libraries that can convert POJO to csv. Some of them allow you to declare schemas and rules.
> This project does not use any 3rd party library because:
> - This aims as a simple example to illustrate iterating/parsing over gauge's SuiteExecutionResult
> - Schema/rules etc are left as implementor's choice

## Running this example

To build the plugin run 

`../../gradlew clean build distro`

**Note the path that you are running this command. This is referred in the install step below**

To install the plugin

***nix systems (linux/osx etc)**

- `mkdir /tmp/sample` 
- `cd /tmp/sample`
- `gauge install csv-report-example -f <path_to_build>/artifacts/csv-report-example-0.0.1.zip`
- `gauge run specs`
- Observe `reports/csv-report-example/result.csv`


**Windows systems**
- `mkdir %TEMP%\sample # on windows cmd`
- `cd %TEMP%\sample`
- `gauge install csv-report-example -f <path_to_build>\artifacts\csv-report-example-0.0.1.zip`
- `gauge run specs`
- Observe `reports\csv-report-example\result.csv`
