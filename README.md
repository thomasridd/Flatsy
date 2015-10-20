# Flatsy

**A database interface for your file system**

 - Create, retrieve, update, delete, and query any file system
 - Additional updates and queries for dealing with json files
 - Query the file system using a simple query language

## Queries

The query language is up and working. Use from an instance of FlatsyCommandLine to run commands individually or as a script  
  
Example: To list all files from a directory with uri ending .flatsy
```
from /Users/Tom/

filter files
filter uri_ends .flatsy

list
```

### Filters

##### Simple filters
```
filter files      // files only
filter folders
filter uri_ends <some string>     // uri ends with value
filter uri_contains <some string> // uri contains a string
filter find <some string>         // file contains a value
```

##### Logic
```
filter not ...    // invert the filter
```

##### JSON Filters
https://github.com/jayway/JsonPath

Files that contain valid JSON
```
filter jsonpath valid
filter jsonpath $.field exists
filter jsonpath $.field equals <value1> <value2> <...>
```

### Actions

##### Simple actions
```
list
list <output file path>
copy <second database route>         // copy files to an identical uri in the second database
folder_copy <second database route>  // copy files and all files in the same directory
replace <old value> <new value>      // replace string in file
```

##### JSON actions
Create table using JsonPath values
```
TABLE $.path1 $.path2 $.path3
TABLE <output file path> $.path1 $.path2 $.path3
```


