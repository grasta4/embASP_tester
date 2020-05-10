# EmbASP tester

## HOW TO

Be sure to have a working version of **EmbASP** and the **ANTLR4 Runtime** already imported in your project.

### Java
  - Import the **.jar** in your project.
  - Provide alternately:
    - an array containing classes representing the predicates in the ASP program to the **_loadClasses_** method;
    - an array with the paths to the **.java** files containing such classes to the **_importClasses_** method.
  - _Same as Python from this point on..._
  
### Python
  - Import the **.egg** in your project.
  - Provide alternately:
    - an array containing classes representing the predicates in the ASP program to the **_load_classes_** method;
    - the path to the directory containing the modules for such classes to the **_import_classes_dir_** method, if you want the tester to import them for you (**IMPORTANT: in this case, use same name, same case for the classes and the .py files**).
  - Provide the **_run_** method with the following ordered paramenters:
    - the **.csv** file where you want solvers' running times to be stored);
    - the directory that contains the solver (*CLINGO*, *DLV* and *DLV2* are supported);
    - the ASP input file;
    - an optional file that contains options for the solvers, separated by a whitespace (see template [here](https://github.com/grasta4/embASP_tester/blob/master/templates/filters.conf)). **IMPORTANT: specify option *-no-option-file* if you are not using this feature**.
  - Execute.
  
### C#
  - You're probably better off using the source files in your project instead of importing the library (we can't guarantee that the **.dll** dependencies will always be up to date).
  - _Follow the steps specified for the other versions..._
