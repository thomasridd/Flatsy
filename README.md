# Flatsy

**A database interface for your file system**

 - Create, retrieve, update, delete, and query any file system
 - Additional updates and queries for dealing with json files

**Flatsy Query Language**

The query language is up and working. Use from an instance of FlatsyCommandLine to run commands individually or as a script  
  
A query takes the form FROM -> FILTER -> ACTIONS

****FROM <Root>****
Select a root for the query

***FILTERS***

****FILTER FILES****
Filter to files

****FILTER FOLDERS****
Filter to folders

****FILTER URI_CONTAINS <Value>****
Filter to objects whose path relative to root contains Value

****FILTER URI_ENDS <Value>****
Filter to paths that end with a value

****FILTER FIND <Value>****
Filter to files that contain a string


***JSON FILTERS***

****FILTER JSONPATH VALID****
Filter to files that contain valid json
Does not check filenames so a .json prefilter would be useful

****FILTER JSONPATH $.path EQUALS value1, value2, value3****



