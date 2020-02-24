# EmbASP tester

## HOW TO

Be sure to have a working version of **EmbASP** and the **ANTLR4 Runtime** already imported in your project.

### Java
- Import the **.jar** in your project.
- Provide resources as arguments to method **_run_** in the following order:
  - the **.csv** file where you want the solvers' running times to be stores (you can download the template at: );
  - the directory that contains the solver (*CLINGO*, *DLV* and *DLV2* are supported);
  - the ASP input file;
  - an optional file that contains options for the solvers (you can download the template at: )
    **IMPORTANT: specify option *-no-option-file* if you are not using this file**;
  - the list of classes representing the predicates in the ASP program, listed one after the other.
 - Execute.
 
### Python
  - Import the **egg** in your project.
  - Same as Java.
  
### C#
  - Coming soon...
