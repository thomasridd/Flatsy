# Flatsy

**A database interface for your file system**

 - Create, retrieve, update, delete, and query any file system
 - Additional updates and queries for dealing with json files

**Flatsy Query Language

The query language is up and working. Use from an instance of FlatsyCommandLine to run commands individually or as a script  
  
A query takes the form FROM -> FILTER -> ACTIONS

Example: To list all files from a directory with uri ending .flatsy
```
FROM /Users/Tom/

FILTER FILES
FILTER URI_ENDS .flatsy

LIST
```

## FILTERS

Files only
```
FILTER FILES
```

Folders only
```
FILTER FOLDERS
```

URIs that contain a value
```
FILTER URI_CONTAINS <Value>
```

URIs that end with a value
```
FILTER URI_ENDS <Value>
```

Files that contain a string
```
FILTER FIND <Value>
```

#### JSON
https://github.com/jayway/JsonPath

Files that contain valid JSON
```
FILTER JSONPATH VALID
```

Files that contain valid JSON for a specific field
```
FILTER JSONPATH $.field EXISTS
```

Files with a json field equal to a value or from a list
```
FILTER JSONPATH $.field EQUALS value

FILTER JSONPATH $.field EQUALS value1, value2, value3
```

## ACTIONS

List uris
```
LIST

LIST <output file path>
```

Copy files to a parallel flat file instance
```
COPY <second database route>
```

Find and Replace all values of a string
```
REPLACE <Old Value> <New Value>
```

Create table using JsonPath values
```
TABLE $.name $.summary $.description.type
```


