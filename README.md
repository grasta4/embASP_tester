# EmbASP tester

## HOW TO

Be sure to have a working version of **EmbASP** and the **ANTLR4 Runtime** already imported in your project.

### Java
  - Coming soon 

### Python
  - Import the **.egg** in your project.
  - Provide alternately:
    - an array containing classes representing the predicates in the ASP program to the **load_classes** method;
    - the path to the directory containing the modules for such classes to the **import_classes_dir** method, if you want the tester to import them for you;
  - Provide the **_run_** method with the following ordered paramenters:
    - the **.csv** file where you want the solvers' running times to be stores (you can download the template at: );
    - the directory that contains the solver (*CLINGO*, *DLV* and *DLV2* are supported);
    - the ASP input file;
    - an optional file that contains options for the solvers (you can download the template at: ). **IMPORTANT: specify option *-no-option-file* if you are not using this feature**;
  - Execute.
  
### C#
  - Coming soon...
