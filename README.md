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

copy <second root>                          // copy files to an identical uri in the second database
folder_copy <second root>                   // copy files and all files in the same directory

copy_to <second root> <expression>          // copy files to a uri in the second database defined by expression
folder_copy_to <second root> <expression>   // copy files and all files in the same directory

replace <old value> <new value>             // replace string in file


```

##### JSON actions
Create table using JsonPath values
```
table $.path1 $.path2 $.path3
table <output file path> $.path1 $.path2 $.path3

json <$.root> put <field> <expression>      // write a value to a json file
json <$.root> add <field> <expression>      // add a value to an array in a json file

```

### Expressions
FlatsyUtil.stringExpression(String expression, FlatsyObject object) is a crude string operation parser
Concatenate strings using the + operator

##### Syntax
```
singleString        // simple value
"1 + 1"             // strings in quotes

~.uri               // the uri of the object
~.file              // the file name of the file (if applicable)
~.parent            // the uri for the parent object (watch out for root)

$.path              // a jsonpath

++                  // join with a space as separator
```
