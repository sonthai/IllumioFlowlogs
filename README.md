# IllumioFlowlogs

- Load the project into Intellij and run FlowLogsApplication class by right click on the file, then choose FlowLogsApplication.main()
- When the project runs, it will preload the lookup table into the memory
- It also asks for the input file and output directory in the CLI prompt (input file is the absolute filt path, for the output directory, it can be any location where the output will be generated
- To make it easy and simple, the input file will only contains 2 columns: destination port anf protocol only and no headers. I add sample.txt file in the repo for the sample input file
- For the look up data file, it will be stored under src/main/resources/data/lookup-data.csv. This file can be updated with different contents fot testing,
